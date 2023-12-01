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

    private static void dump(FlowNode s, int indent) {
        System.out.println("[" + s.getElementType().getTypeName() + "] " + s.getName());
        if (s instanceof BusinessRuleTask dt) {
            System.out.println(dt);
        }

        Collection<SequenceFlow> outgoing = s.getOutgoing();
        for (SequenceFlow sf : outgoing) {
            //ConditionExpression co = s.getModelInstance().newInstance(ConditionExpression.class);
            //co.setTextContent("condizione");
            //sf.setConditionExpression(co);

            System.out.print(" ".repeat(indent + 1) + "-> ");
            System.out.print("<" + (sf.getName() != null ? sf.getName() : "") + (sf.getConditionExpression() != null ? sf.getConditionExpression().getTextContent() : "") + ">");
            System.out.print(" ");
            FlowNode t = sf.getTarget();

            dump(t, indent + 3);
        }
    }

    private static void dump(DecisionTable t, int indent) {
        System.out.println("[" + t.getElementType().getTypeName() + "] " + t.getAttributeValue("name"));
        System.out.println("* Inputs:");
        List<Input> inputs = new ArrayList(t.getInputs());
        for (Input i : inputs) {
            System.out.println("** [" + i.getInputExpression().getAttributeValue("typeRef") + "] " + i.getInputExpression().getText().getTextContent());
        }
        System.out.println("* Outputs:");
        List<Output> outputs = new ArrayList(t.getOutputs());
        for (Output o : outputs) {
            System.out.println("** [" + o.getAttributeValue("typeRef") + "] " + o.getAttributeValue("name"));
        }
        System.out.println("* Rules:");
        Collection<Rule> rules = t.getRules();
        int rule_counter = 0;
        for (Rule r : rules) {
            System.out.println("** " + (++rule_counter) + " [" + r.getDescription().getTextContent() + "]");
            int sub_counter = 0;
            Collection<InputEntry> inputEntries = r.getInputEntries();
            System.out.print("*** WHEN ");
            for (InputEntry i : inputEntries) {
                System.out.print((sub_counter > 0 ? " AND " : "") + "(" + (i.getExpressionLanguage() != null ? "[" + i.getExpressionLanguage() + "] " : "")
                        + inputs.get(sub_counter++).getInputExpression().getText().getTextContent() + " "
                        + i.getTextContent() + ")");
            }
            System.out.println();
            sub_counter = 0;
            System.out.print("*** THEN ");
            Collection<OutputEntry> outputEntries = r.getOutputEntries();
            for (OutputEntry o : outputEntries) {
                System.out.print((sub_counter > 0 ? ", " : "") + (o.getExpressionLanguage() != null ? "[" + o.getExpressionLanguage() + "] " : "")
                        + outputs.get(sub_counter++).getAttributeValue("name") + " = "
                        + o.getTextContent());
            }
            System.out.println();
        }
    }

    private static void dump(DmnModelInstance dmn) {
        Collection<DecisionTable> tables = dmn.getModelElementsByType(DecisionTable.class);
        for (DecisionTable t : tables) {
            dump(t, 0);
        }
    }

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
        //System.out.println(ft.translate("abs(x)>1 and (a=\"w\" or b in [1..4])"));
        //System.out.println(ft.translate("[1..4]"));
        //System.exit(0);

//        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("C:\\Users\\giuse\\Desktop\\diagram_1.bpmn"));
//        Collection<StartEvent> start = modelInstance.getModelElementsByType(StartEvent.class);
//        for (StartEvent s : start) {
//            dump(s, 0);
//        }
        DmnModelInstance dmnInstance = Dmn.readModelFromFile(new File("C:\\Users\\giuse\\Desktop\\diagram_1.dmn"));
        DMNTranslator dt = new ToJavaDMNTranslator();
        dump(dmnInstance);
        Map<String, String> dtt = dt.translateDecisionTables(dmnInstance);
        for (String dtn : dtt.keySet()) {
            System.out.println(dtt.get(dtn));
        }
//Bpmn.writeModelToFile(new File("test.bpmn"), modelInstance);
    }
}
