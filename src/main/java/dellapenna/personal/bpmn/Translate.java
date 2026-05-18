package dellapenna.personal.bpmn;

import dellapenna.personal.util.JarTool;
import dellapenna.personal.util.OutputManager;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Giuseppe Della Penna
 */
public class Translate {

    private static final OutputManager outlog = new OutputManager();
    private final Options options;

    public void doTranslation(String[] files, BPMNTranslationInfo t_info) {

        BPDMNTranslator t = new BPDMNTranslator();
        Path[] inputs = new Path[files.length];

        Path base_folder = Paths.get(".").toAbsolutePath().normalize();

        for (int i = 0; i < files.length; ++i) {
            inputs[i] = Path.of(files[i]);
        }

        try {
            Path base_output_folder = base_folder;
            List<BPDMNTranslator.GenerationInfo> generated_code = t.compile_inputs(base_output_folder, t_info, inputs);

            ToolProvider compiler = ToolProvider.findFirst("javac").orElse(null);

            for (BPDMNTranslator.GenerationInfo gen_info : generated_code) {
                outlog.emit(0, "Compiler", "compiling process " + gen_info.output_java_file);
                Path target_output_folder = Files.createTempDirectory(base_output_folder, "compile");
                Path target_java = target_output_folder.resolve(gen_info.output_java_file.getFileName());
                Files.copy(gen_info.output_java_file, target_java);

                outlog.emit(1, "Compiler", "java source written to " + gen_info.output_java_file);
                outlog.emit(1, "Compiler", "input variables written to " + gen_info.output_inputs_file);
                outlog.emit(1, "Compiler", "process graph written to  " + gen_info.output_graph_file);

                String sfn = gen_info.output_java_file.getFileName().toString();
                String cn = sfn.substring(0, sfn.lastIndexOf("."));
                String jfn = cn + ".jar";
                Path output_jar_file = base_output_folder.resolve(jfn);

                if (compiler != null) {
                    //outlog.emit(2, "Compiler", "using " + compiler.description());
                    compiler.run(System.out, System.err, target_java.toString());
                    outlog.emit(1, "Compiler", "creating jar file " + output_jar_file);
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
                    outlog.emit(2, "Compiler", "type java -jar " + output_jar_file + " to execute the process. Make sure that the BPMNTranslator or BPMNRuntime jar is in the current directory. Requires Java 22+.");
                } else {
                    outlog.emit(OutputManager.MessageType.WARNING, 1, "Compiler", "java compiler cannot be automatically invoked. Please invoke manually the following command line to compile the process: javac -cp BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar " + target_java.toString());
                    outlog.emit(OutputManager.MessageType.WARNING, 1, "Compiler", "you can then use the jar command to pack all the resulting classes in " + output_jar_file + ", with Manifest Main-Class " + cn + " and Class-Path BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar BPMNModelTranslator-1.0-SNAPSHOT.jar BPMNModelRuntime.jar");
                }

            }
        } catch (IOException | BDTransException ex) {
            outlog.emit(OutputManager.MessageType.ERROR, 1, "Translator", ex.getMessage());
        }
    }

    public Translate() {

        options = new Options();
        options.addOption(Option.builder("fi")
                .argName("var_name1,var_name2,...")
                .hasArg()
                .desc("Force input variables")
                .build());
        options.addOption(Option.builder("np")
                .desc("Use sequentialized parallel")
                .build());
        options.addOption(Option.builder("nd")
                .desc("Disable debug messages in generated java process")
                .build());
        options.addOption(Option.builder("nt")
                .desc("Disable run trace creation in generated java process")
                .build());
        options.addOption(Option.builder("dw")
                .desc("Generate dummy workload for empty tasks")
                .build());
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BDTrans [<OPTIONS>] <FILES>", options);
    }

    public void parseCommandLine(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.getArgs().length <= 0) {
                printHelp();
            } else {
                BPMNTranslationInfo t_info = new BPMNTranslationInfo();
                t_info.setTrueParallel(!cmd.hasOption("sp"));
                outlog.emit(0, "CommandLine", "using " + (t_info.isTrueParallel() ? "true parallel" : "sequentialized parallel") + " mode");
                t_info.setDebug(!cmd.hasOption("nd"));
                if (t_info.isDebug()) {
                    outlog.emit(0, "CommandLine", "java debug mode enabled");
                } 
                t_info.setDummyWorkload(cmd.hasOption("dw"));
                if (t_info.isDummyWorkload()) {
                    outlog.emit(0, "CommandLine", "generating dummy workload for empty tasks");
                } 
                t_info.setTrace(!cmd.hasOption("nt"));
                if (t_info.isTrace()) {
                    outlog.emit(0, "CommandLine", "trace generation enabled");
                }
                if (cmd.hasOption("fi")) {
                    for (String v : cmd.getOptionValue("fi").split(",")) {
                        t_info.addForcedInputVariable(v);
                    }
                    outlog.emit(0, "CommandLine", "forcing input variables " + t_info.getForcedInputVariables().stream().collect(Collectors.joining(",")));
                }
                doTranslation(cmd.getArgs(), t_info);
            }
        } catch (ParseException ex) {
            outlog.emit(OutputManager.MessageType.ERROR, 0, "CommandLine", ex.getMessage());
            printHelp();
        }
    }

    public static void main(String[] args) {
        Translate instance = new Translate();
        instance.parseCommandLine(args);

    }
}
