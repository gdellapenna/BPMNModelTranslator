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
    public T translate(DecisionTable t, DMNTranslationInfo info) throws FeelTranslatorException {
        List<Input> inputs = new ArrayList(t.getInputs());
        List<Output> outputs = new ArrayList(t.getOutputs());
        List<DMNDecisionRule<T>> decoded_rules = new ArrayList<>();
        Collection<Rule> rules = t.getRules();
        DMNTranslationInfo lhsinfo = new DMNTranslationInfo(info);
        DMNTranslationInfo rhsinfo = new DMNTranslationInfo(info);
        lhsinfo.getReadVariables().clear(); //ignored
        rhsinfo.getReadVariables().clear();

        for (Rule r : rules) {
            String rule_comment = r.getDescription() != null ? r.getDescription().getTextContent() : "";
            int ref_counter = 0;
            List<DMNCondition<T>> rule_conditions = new ArrayList<>();
            Collection<InputEntry> inputEntries = r.getInputEntries();

            for (InputEntry i : inputEntries) {
                if (!i.getTextContent().isBlank()) { //blank significa "don't care"
                    rule_conditions.add(new DMNCondition<>(
                            translateExpression(null, inputs.get(ref_counter).getInputExpression().getTextContent(), lhsinfo),
                            translateExpression(inputs.get(ref_counter++).getInputExpression().getTextContent(), i.getTextContent(), lhsinfo)
                    ));
                } else {
                    rule_conditions.add(new DMNCondition<>(
                            translateExpression(null, inputs.get(ref_counter++).getInputExpression().getTextContent(), lhsinfo),
                            null));
                }
            }
            ref_counter = 0;
            List<DMNAssignment<T>> rule_assignments = new ArrayList<>();
            Collection<OutputEntry> outputEntries = r.getOutputEntries();
            for (OutputEntry o : outputEntries) {
                rule_assignments.add(new DMNAssignment<>(
                        outputs.get(ref_counter++).getAttributeValue("name"),
                        translateExpression(null, o.getTextContent(), rhsinfo)
                ));
            }
            decoded_rules.add(new DMNDecisionRule<>(rule_conditions, rule_assignments, rule_comment));
        }

        info.getReadVariables().addAll(rhsinfo.getReadVariables());
        return generateDecisionTableSource(t.getParentElement().getAttributeValue("id"), inputs, outputs, decoded_rules, info);
    }

    @Override
    public T translate(DmnModelInstance dmn, DMNTranslationInfo info) throws FeelTranslatorException {
        Map<String, T> decoded_tables = new HashMap<>();
        Collection<DecisionTable> tables = dmn.getModelElementsByType(DecisionTable.class);
        for (DecisionTable t : tables) {
            decoded_tables.put(t.getParentElement().getAttributeValue("id"), translate(t, info));
        }
        return generateDecisionModelSource(decoded_tables, info);
    }

    protected abstract T translateExpression(String input, String exp, DMNTranslationInfo info) throws FeelTranslatorException;

    protected abstract T generateRulesSource(List<DMNDecisionRule<T>> decoded_rules, String output_record_name, DMNTranslationInfo info);

    protected abstract T generateDecisionTableSource(String id, List<Input> inputs, List<Output> outputs, List<DMNDecisionRule<T>> decoded_rules, DMNTranslationInfo info);

    protected abstract T generateDecisionModelSource(Map<String, T> decoded_tables, DMNTranslationInfo info);

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
