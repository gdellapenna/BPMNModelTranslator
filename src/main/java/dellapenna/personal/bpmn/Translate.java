package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 *
 * @author giuse
 */
public class Translate {

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException, Exception {

        BPDMNTranslator t = new BPDMNTranslator();
        Path[] inputs = new Path[args.length];
        for (int i = 0; i < args.length; ++i) {
            inputs[i] = Path.of(args[i]);
        }
        List<Path> compiled_java_files = t.compile_inputs(inputs);
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        for (Path source : compiled_java_files) {
            System.out.println("compiling " + source);
            compiler.run(null, null, null, source.toString());
        }

//https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class        
//URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
//Class<?> cls = Class.forName("test.Test", true, classLoader);
//Object instance = cls.getDeclaredConstructor().newInstance();
    }
}
