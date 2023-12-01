package dellapenna.personal.bpmnmodeltest;

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
    public T translateDecisionTable(DecisionTable t) throws FeelTranslatorException {
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
        List<DecisionRule<T>> decoded_rules = new ArrayList<>();
        Collection<Rule> rules = t.getRules();
        for (Rule r : rules) {
            String rule_comment = r.getDescription().getTextContent();
            int ref_counter = 0;
            List<Condition<T>> rule_conditions = new ArrayList<>();
            Collection<InputEntry> inputEntries = r.getInputEntries();
            for (InputEntry i : inputEntries) {
                rule_conditions.add(new Condition<>(
                        translateExpression(null, inputs.get(ref_counter).getInputExpression().getTextContent()),
                        translateExpression(inputs.get(ref_counter++).getInputExpression().getTextContent(), i.getTextContent())
                ));
            }
            ref_counter = 0;
            List<Assignment<T>> rule_assignments = new ArrayList<>();
            Collection<OutputEntry> outputEntries = r.getOutputEntries();
            for (OutputEntry o : outputEntries) {
                rule_assignments.add(new Assignment<>(
                        outputs.get(ref_counter++).getAttributeValue("name"),
                        translateExpression(null, o.getTextContent())
                ));
            }
            decoded_rules.add(new DecisionRule<>(rule_conditions, rule_assignments, rule_comment));
        }
        return translateRules(decoded_rules);
    }

    @Override
    public Map<String, T> translateDecisionTables(DmnModelInstance dmn) throws FeelTranslatorException {
        Map<String, T> result = new HashMap<>();
        Collection<DecisionTable> tables = dmn.getModelElementsByType(DecisionTable.class);
        for (DecisionTable t : tables) {
            result.put(t.getId(), translateDecisionTable(t));
        }
        return result;
    }

    protected abstract T translateExpression(String input, String exp) throws FeelTranslatorException;

    protected abstract T translateRules(List<DecisionRule<T>> decoded_rules);

}
