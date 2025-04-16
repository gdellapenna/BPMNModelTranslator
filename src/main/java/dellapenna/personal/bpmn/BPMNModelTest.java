package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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

        System.out.println(t.translate(expression, null));

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

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException, Exception {

        BPDMNTranslator t = new BPDMNTranslator();
        
        Path output_folder = Paths.get(".").toAbsolutePath().normalize();

//        t.compile_inputs(output_folder,Path.of("surgery_tables.dmn"), Path.of("Surgery.bpmn"));
//
//        t.compile_inputs(output_folder,Path.of("get_length.dmn"), Path.of("determine_mode.dmn"), Path.of("choose_consent.dmn"), Path.of("Shipment.bpmn"));
//
//        t.compile_inputs(output_folder,Path.of("Loop.bpmn"));
//
//        t.compile_inputs(output_folder,Path.of("Parallel1.bpmn"));
//
//        t.compile_inputs(output_folder,Path.of("Parallel2.bpmn"));
//        
//        t.compile_inputs(output_folder,Path.of("Events.bpmn"));
//        t.compile_inputs(output_folder,Path.of("Parallel1Err.bpmn"));
//        
        t.compile_inputs(output_folder,Path.of("Simple_Message.bpmn"));

        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.generateBpmnSource("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.generateBpmnSource("a.b=c"));        
    }
}
