package dellapenna.personal.bpmnmodeltest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
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

    public static void main(String[] args) throws FeelTranslatorException {
        FeelTranslator ft = new ToJavaFeelTranslator();
        System.out.println(ft.translate("abs(x)>1 and (a=\"w\" or (b in [1..4]))"));
        //System.out.println(ft.translate("[1..4]"));        

        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("C:\\Users\\giuse\\Desktop\\diagram_1.dmn"));
        DMNTranslator dt = new ToJavaDMNTranslator();
        ((AbstractDMNTranslator) dt).dump(dmnInstance);
        Map<String, String> dtt = dt.translate(dmnInstance);
        for (String dtn : dtt.keySet()) {
            System.out.println(dtt.get(dtn));
        }
        
                BpmnModelInstance bpmnInstance = Bpmn.readModelFromFile(new File("C:\\Users\\giuse\\Desktop\\diagram_1.bpmn"));
        BPMNTranslator bt = new AbstractBPMNTranslator<String>();
        ((AbstractBPMNTranslator) bt).dump(bpmnInstance);

        
//Bpmn.writeModelToFile(new File("test.bpmn"), modelInstance);
    }
}
