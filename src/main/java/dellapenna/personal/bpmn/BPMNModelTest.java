package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNTranslator;
import dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator;
import dellapenna.personal.bpmn.dmn.DMNTranslator;
import dellapenna.personal.bpmn.dmn.ToJavaDMNTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public static String pre_code() {
        return """
                             
               
               class prova {
               """;
    }
    
    public static String post_code() {
        return """
               }
               """;
    }


    public static void main(String[] args) throws FeelTranslatorException, IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("output.java"));
        
        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.translate("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.translate("a.b=c"));        
//

        out.write(pre_code());
        out.newLine();

        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("diagram_1.dmn"));
        DMNTranslator<String> dt = new ToJavaDMNTranslator();
        //((AbstractDMNTranslator) dt).dump(dmnInstance);
        out.write(dt.translate(dmnInstance));

        out.newLine();
        out.newLine();
//
        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("diagram_1.bpmn"));
        BPMNTranslator<String> bt = new ToJavaBPMNTranslator();
        //((AbstractBPMNTranslator) bt).dump(bpmnInstance);
        out.write(bt.translate(bpmnInstance));
        
        out.newLine();
        out.write(post_code());
//        
        out.close();

//Bpmn.writeModelToFile(new File("test.bpmn"), modelInstance);
    }
}
