package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author giuse
 */
public class Translate {

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException, Exception {

        BPDMNTranslator t = new BPDMNTranslator();
        Path[] inputs = new Path[args.length];

        Path base_folder = Paths.get(".").toAbsolutePath().normalize();

        for (int i = 0; i < args.length; ++i) {
            inputs[i] = Path.of(args[i]);
        }

        //Path base_output_folder = Files.createTempDirectory(base_folder, "bpmntemp");
        Path base_output_folder = base_folder;
        List<BPDMNTranslator.GenerationInfo> generated_code = t.compile_inputs(base_output_folder, inputs);

        ToolProvider compiler = ToolProvider.findFirst("javac").orElse(null);
        //ToolProvider packager = ToolProvider.findFirst("jar").orElse(null);

        for (BPDMNTranslator.GenerationInfo gen_info : generated_code) {
            System.out.println("compiling process " + gen_info.output_java_file);
            Path target_output_folder = Files.createTempDirectory(base_output_folder, "compile");
            Path target_java = target_output_folder.resolve(gen_info.output_java_file.getFileName());
            Files.copy(gen_info.output_java_file, target_java);

            String sfn = gen_info.output_java_file.getFileName().toString();
            String cn = sfn.substring(0, sfn.lastIndexOf("."));
            String jfn = cn + ".jar";
            Path output_jar_file = base_output_folder.resolve(jfn);

            if (compiler != null) {
                compiler.run(System.out, System.err, target_java.toString());
                JarTool tool = new JarTool();
                tool.startManifest();
                tool.addToManifest("Main-Class", cn);
                tool.addToManifest("Class-Path", "BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar BPMNModelTranslator-1.0-SNAPSHOT.jar BPMNModelRuntime.jar");

                try (JarOutputStream jar = tool.openJar(output_jar_file.toString())) {
                    try (Stream<Path> stream = Files.list(target_output_folder)) {
                        stream
                                .filter(f -> f.getFileName().toString().endsWith(".class"))
                                .forEach(f -> {
                                    try {
                                        tool.addFileFromPath(jar, target_output_folder.toString(), f);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });
                    }
                    tool.addFileWithPath(jar, "doc/" + gen_info.output_graph_file.getFileName().toString(), gen_info.output_graph_file);
                    tool.addFileWithPath(jar, "doc/" + gen_info.output_inputs_file.getFileName().toString(), gen_info.output_inputs_file);
                    tool.addFileWithPath(jar, "src/" + gen_info.output_java_file.getFileName().toString(), gen_info.output_java_file);
                }
                try (Stream<Path> paths = Files.walk(target_output_folder)) {
                    paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
            } else {
                System.out.println("java compiler cannot be automatically invoked. Please invoke manually the following command line to compile the process: javac -cp BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar " + target_java.toString());
                System.out.println("you can then use the jar command to pack all the resulting classes in "+output_jar_file+", with Manifest Main-Class " + cn + " and Class-Path BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar BPMNModelTranslator-1.0-SNAPSHOT.jar BPMNModelRuntime.jar");
            }
            System.out.println("type java -jar " + output_jar_file + " to execute the process. Make sure that the BPMNTranslator or BPMNRuntime jar is in the current directory. Requires Java 22+.");
        }

//https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class        
//URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
//Class<?> cls = Class.forName("test.Test", true, classLoader);
//Object instance = cls.getDeclaredConstructor().newInstance();
    }
}
