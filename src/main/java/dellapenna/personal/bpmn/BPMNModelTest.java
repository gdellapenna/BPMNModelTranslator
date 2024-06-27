package dellapenna.personal.bpmn;

import dellapenna.personal.bpmn.bpmn.BPMNTranslator;
import dellapenna.personal.bpmn.bpmn.BpmnTranslatorException;
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
            class TypeUtils {

                public static Double tonumber(Object o) {
                    if (o instanceof Number n) {
                        return n.doubleValue();
                    } else {
                        try {
                            return Double.valueOf(o.toString());
                        } catch (NumberFormatException ex) {
                            return 0.0; //should raise an exception
                        }
                    }
                }

                public static String tostring(Object o) {
                    return o.toString();
                }

                public static Boolean toboolean(Object o) {
                    if (o instanceof Boolean b) {
                        return b;
                    } else if (o instanceof Number n) {
                        return n.doubleValue() != 0;
                    } else {
                        return Boolean.valueOf(o.toString());

                    }
                }
            }
            """;
    }

    public static String post_code() {
        return "";
    }

    public static void paperTranslation() throws IOException, FeelTranslatorException, BpmnTranslatorException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("paper.java"))) {

            out.write(pre_code());
            out.newLine();
            
            DMNTranslator<String> dt = new ToJavaDMNTranslator();
            BPMNTranslator<String> bt = new ToJavaBPMNTranslator();

            DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("get_length.dmn"));
            out.write(dt.translate(dmnInstance));
            dmnInstance = Dmn.readModelFromFile(new File("determine_mode.dmn"));
            out.write(dt.translate(dmnInstance));
            dmnInstance = Dmn.readModelFromFile(new File("choose_consent.dmn"));
            out.write(dt.translate(dmnInstance));

            out.newLine();
            out.newLine();

            BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("diagram_paper.bpmn"));
            out.write(bt.translateBpmn(bpmnInstance));

            out.newLine();
            out.write(post_code());
        }

    }

    public static void main(String[] args) throws FeelTranslatorException, IOException, BpmnTranslatorException {
        paperTranslation();
        //System.exit(0);

        BufferedWriter out = new BufferedWriter(new FileWriter("output.java"));

        //FeelTranslator ft = new ToJavaFeelTranslator();
        //System.out.println(ft.translateBpmn("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.translateBpmn("a.b=c"));        
//
        out.write(pre_code());
        out.newLine();

//        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("diagram_1.dmn"));
//        DMNTranslator<String> dt = new ToJavaDMNTranslator();
//        //((AbstractDMNTranslator) dt).dump(dmnInstance);
//        out.write(dt.translate(dmnInstance));
//
//        out.newLine();
//        out.newLine();
//
        BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("diagram_2.bpmn"));
        BPMNTranslator<String> bt = new ToJavaBPMNTranslator();
        //((AbstractBPMNTranslator) bt).dump(bpmnInstance);
        out.write(bt.translateBpmn(bpmnInstance));

        out.newLine();
        out.write(post_code());
//        
        out.close();

//Bpmn.writeModelToFile(new File("test.bpmn"), modelInstance);
    }
}
