package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;

/**
 *
 * @author giuse
 * @param <T>
 */
public abstract class AbstractDMNTranslator<T> implements DMNTranslator<T> {

    @Override
    public T translate(DecisionTable t) throws FeelTranslatorException {
//        System.out.println("[" + t.getElementType().getTypeName() + "] " + t.getAttributeValue("name"));
//        System.out.println("* Inputs:");
        List<Input> inputs = new ArrayList(t.getInputs());
//        for (Input i : inputs) {
//            System.out.println("** [" + i.getInputExpression().getAttributeValue("typeRef") + "] " + i.getInputExpression().getText().getTextContent());
//        }
//        //
//        System.out.println("* Outputs:");
        List<Output> outputs = new ArrayList(t.getOutputs());
//        for (Output o : outputs) {
//            System.out.println("** [" + o.getAttributeValue("typeRef") + "] " + o.getAttributeValue("name"));
//        }
//        //
        List<DMNDecisionRule<T>> decoded_rules = new ArrayList<>();
        Collection<Rule> rules = t.getRules();
        for (Rule r : rules) {
            String rule_comment = r.getDescription().getTextContent();
            int ref_counter = 0;
            List<DMNCondition<T>> rule_conditions = new ArrayList<>();
            Collection<InputEntry> inputEntries = r.getInputEntries();
            for (InputEntry i : inputEntries) {
                rule_conditions.add(new DMNCondition<>(
                        translateExpression(null, inputs.get(ref_counter).getInputExpression().getTextContent()),
                        translateExpression(inputs.get(ref_counter++).getInputExpression().getTextContent(), i.getTextContent())
                ));
            }
            ref_counter = 0;
            List<DMNAssignment<T>> rule_assignments = new ArrayList<>();
            Collection<OutputEntry> outputEntries = r.getOutputEntries();
            for (OutputEntry o : outputEntries) {
                rule_assignments.add(new DMNAssignment<>(
                        outputs.get(ref_counter++).getAttributeValue("name"),
                        translateExpression(null, o.getTextContent())
                ));
            }
            decoded_rules.add(new DMNDecisionRule<>(rule_conditions, rule_assignments, rule_comment));
        }
        return translateRules(decoded_rules);
    }

    @Override
    public Map<String, T> translate(DmnModelInstance dmn) throws FeelTranslatorException {
        Map<String, T> result = new HashMap<>();
        Collection<DecisionTable> tables = dmn.getModelElementsByType(DecisionTable.class);
        for (DecisionTable t : tables) {
            result.put(t.getId(), translate(t));
        }
        return result;
    }

    protected abstract T translateExpression(String input, String exp) throws FeelTranslatorException;

    protected abstract T translateRules(List<DMNDecisionRule<T>> decoded_rules);

    //////////////
    
    
    public void dump(DecisionTable t) {
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

    public void dump(DmnModelInstance dmn) {
        Collection<DecisionTable> tables = dmn.getModelElementsByType(DecisionTable.class);
        for (DecisionTable t : tables) {
            dump(t);
        }
    }

}
