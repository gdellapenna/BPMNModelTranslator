package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
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

        Path base_output_folder = Files.createTempDirectory(base_folder, "bpmntemp");
        List<Path> compiled_java_files = t.compile_inputs(base_output_folder, inputs);

        ToolProvider compiler = ToolProvider.findFirst("javac").orElse(null);
        //ToolProvider packager = ToolProvider.findFirst("jar").orElse(null);
        if (compiler != null) {
            for (Path source : compiled_java_files) {
                System.out.println("compiling " + source);
                Path target_output_folder = Files.createTempDirectory(base_output_folder, "compile-" + source.getFileName());
                Path target_java = target_output_folder.resolve(source.getFileName());
                Files.copy(source, target_java);
                compiler.run(System.out, System.err, target_java.toString());
                //if (packager != null) {
                String sfn = source.getFileName().toString();
                String cn = sfn.substring(0, sfn.lastIndexOf("."));
                String jfn = cn + ".jar";
                //packager.run(System.out, System.err, "-cfe", base_output_folder.resolve(jfn).toString(), cn, target_output_folder.toString()+"/*.class");                    
                //packager.run(System.out, System.err, "--create", "--file " + base_output_folder.resolve(jfn).toString(), "-C " + target_output_folder.toString());

                JarTool tool = new JarTool();
                tool.startManifest();
                tool.addToManifest("Main-Class", cn);
                tool.addToManifest("Class-Path", "BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar");
                
                try (JarOutputStream jar = tool.openJar(base_output_folder.resolve(jfn).toString())) {
                    try (Stream<Path> stream = Files.list(target_output_folder)) {
                        //stream.forEach(f->{System.out.println(f+" "+f.getFileName()+f.getFileName().toString().endsWith(".class"));});
                        stream.filter(f->f.getFileName().toString().endsWith(".class"))
                                .forEach(f -> {
                                    try {
                                        tool.addFile(jar, target_output_folder.toString(), f.toString());
                                    } catch (IOException ex) {
                                        Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });
                    }
                }
            }
        }

//https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class        
//URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
//Class<?> cls = Class.forName("test.Test", true, classLoader);
//Object instance = cls.getDeclaredConstructor().newInstance();
    }
}
