package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.AbstractBPMNTranslator;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.bpmn.Options;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.feel.FeelEngine;
import org.camunda.feel.api.EvaluationResult;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.ParseResult;
import org.camunda.feel.impl.SpiServiceLoader;

/**
 *
 * @author giuse
 */
public class BPMNModelTest {

    public static void testFeel(String expression) throws FeelTranslatorException {
        FeelTranslator t = new ToJavaFeelTranslator();

        System.out.println(t.translate(expression));

        //////
        FeelEngine engine = new FeelEngine.Builder()
                .valueMapper(SpiServiceLoader.loadValueMapper())
                .functionProvider(SpiServiceLoader.loadFunctionProvider())
                .build();
        FeelEngineApi api = new FeelEngineApi(engine);

        final Map<String, Object> variables = Map.of("x", 21);

        ParseResult parsing = api.parseExpression(expression);
        if (parsing.isSuccess()) {
            EvaluationResult evaluating = api.evaluate(parsing.parsedExpression(), variables);
            if (evaluating.isSuccess()) {
                System.out.println("result is " + evaluating.result());
            } else {
                System.err.println("evaluation error: " + evaluating.failure().message());
            }
        } else {
            System.err.println("parsing error: " + parsing.failure().message());
        }
    }

    ////////////////////////////////////////
    
    public static final String PRE_CODE= "import dellapenna.personal.bpmn.exec.*;";
    public static final String POST_CODE= "";
    

    public static void compile(Path[] dmn_files, Path bpmn_file)
            throws IOException, FeelTranslatorException, BpmnTranslatorException {
        Options opt = new Options();
        opt.setDebug(true);
        opt.setTrueParallel(true);

        Path output_file = bpmn_file.toAbsolutePath().getParent().resolve(bpmn_file.getFileName() + ".java");

        try (BufferedWriter out = new BufferedWriter(new FileWriter(output_file.toFile()))) {

            out.write(PRE_CODE);
            out.newLine();
            out.newLine();

            DMNTranslator<String> dt = new ToJavaDMNTranslator();
            AbstractBPMNTranslator bt = new ToJavaBPMNTranslator();

            for (Path dmn_file : dmn_files) {
                DmnModelInstance dmnInstance = Dmn.readModelFromFile(dmn_file.toFile());
                out.write(dt.translate(dmnInstance));
            }
            out.newLine();
            out.newLine();

            BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(bpmn_file.toFile());
            out.write(bt.generateBpmnSource(bt.decodeBpmn(bpmnInstance, opt), opt));

            out.newLine();
            out.write(POST_CODE);
        }

    }

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException {

        compile(new Path[]{Path.of("get_length.dmn"), Path.of("determine_mode.dmn"), Path.of("choose_consent.dmn")}, Path.of("diagram_paper.bpmn"));

        compile(new Path[0], Path.of("diagram_loop.bpmn"));

        compile(new Path[0], Path.of("diagram_2_ext.bpmn"));

        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.generateBpmnSource("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.generateBpmnSource("a.b=c"));        
//
//        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("diagram_1.dmn"));
//        DMNTranslator<String> dt = new ToJavaDMNTranslator();
//        //((AbstractDMNTranslator) dt).dump(dmnInstance);
//        out.write(dt.translate(dmnInstance));
//
//        out.newLine();
//        out.newLine();
//
        //((AbstractBPMNTranslator) bt).dump(bpmnInstance);
//Bpmn.writeModelToFile(new File("compile.bpmn"), modelInstance);
    }
}
