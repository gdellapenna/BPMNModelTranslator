package dellapenna.personal.bpmn.versim;

import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import dellapenna.personal.bpmn.bpmn.VariableDefinition;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.dmn.DMNDecodedTable;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.camunda.feel.syntaxtree.ConstNumber;
import org.camunda.feel.syntaxtree.ConstRangeBoundary;
import org.camunda.feel.syntaxtree.ConstString;
import org.camunda.feel.syntaxtree.Exp;
import org.camunda.feel.syntaxtree.InputGreaterOrEqual;
import org.camunda.feel.syntaxtree.InputGreaterThan;
import org.camunda.feel.syntaxtree.InputInRange;
import org.camunda.feel.syntaxtree.InputLessOrEqual;
import org.camunda.feel.syntaxtree.InputLessThan;
import org.camunda.feel.syntaxtree.OpenConstRangeBoundary;

/**
 *
 * @author giuse
 */
public class VariableUtils {

    FeelTranslator<String> ft = new ToJavaFeelTranslator();

    public void decodeCostraint(String condition_expression, VariableDefinition.VariableBounds b) throws FeelTranslatorException {
        if (!condition_expression.isBlank()) {
            Exp exp = ft.parse(condition_expression);
            //System.out.println(exp.getClass().getName());
            switch (exp) {
                case ConstString texp ->
                    b.addCase(texp.value());
                case ConstNumber texp ->
                    b.updateRange(texp.value().bigDecimal().doubleValue());
                case InputInRange texp -> {
                    ConstRangeBoundary left = texp.range().start();
                    ConstRangeBoundary right = texp.range().end();
                    //TODO gestire gli open e i close
                    if (left.value() instanceof ConstNumber n) {
                        b.updateMin(n.value().bigDecimal().doubleValue(), (left instanceof OpenConstRangeBoundary));
                    }
                    if (right.value() instanceof ConstNumber n) {
                        b.updateMax(n.value().bigDecimal().doubleValue(), (right instanceof OpenConstRangeBoundary));
                    }
                }
                case InputGreaterThan texp -> {
                    if (texp.x() instanceof ConstNumber n) {
                        b.updateMin(n.value().bigDecimal().doubleValue(), true);
                    }
                }
                case InputLessThan texp -> {
                    if (texp.x() instanceof ConstNumber n) {
                        b.updateMax(n.value().bigDecimal().doubleValue(), true);
                    }
                }
                case InputGreaterOrEqual texp -> {
                    if (texp.x() instanceof ConstNumber n) {
                        b.updateMin(n.value().bigDecimal().doubleValue(), false);
                    }
                }
                case InputLessOrEqual texp -> {
                    if (texp.x() instanceof ConstNumber n) {
                        b.updateMax(n.value().bigDecimal().doubleValue(), false);
                    }
                }
                default -> {
                    b.addExpression(condition_expression); //non auto-deducibile
                }
            }
            //gestire gli altri operatori di confronto
        } else {
            //qui è un don't care quindi direi che non collezioniamo alcun vincolo
        }
    }

    public List<String> extractDMNConstraints(DMNDecodedModel<String>[] dmns, String table_id, String input_name) {
        DMNDecodedTable<String> table = Arrays.stream(dmns).flatMap(dmn -> dmn.tables().values().stream()).filter(t -> t.id().equals(table_id)).findFirst().orElse(null);
        if (table != null) {
            return table.rules().stream().flatMap(r -> r.conditions().stream()).filter(c -> c.inputExpression().equals(input_name)).map(c -> c.sourceTestExpression()).toList();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public VariableDefinition.VariableBounds analyzeInputConstraints(VariableDefinition v, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        //System.out.print("* INPUT " + v.getName() + " USATO IN ");
        //System.out.println(v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().map(u -> u.sourceId() + " (" + u.sourceExpression() + ")").collect(Collectors.joining(", ")));
        v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().forEach(u -> {
            if (v.getName().equals(u.sourceExpression())) {
                //System.out.print("DIRECT USAGE: " + u.sourceId());
                if (u.sourceId().startsWith("$DMN$")) {
                    String[] parts = u.sourceId().substring(5).split("\\$");
                    //System.out.print("DMN TABLE " + parts[0] + " INPUT " + parts[1] + " CASES ");
                    List<String> condition_expressions = extractDMNConstraints(dmns, parts[0], parts[1]);
                    //System.out.print(condition_expressions.stream().collect(Collectors.joining(", ")));
                    for (String c : condition_expressions) {
                        try {
                            decodeCostraint(c, v.getBounds());
                        } catch (FeelTranslatorException ex) {
                            Logger.getLogger(VariableUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //System.out.println();

            }
        });
        //System.out.println(v.getBounds());
        return v.getBounds();
    }

    public void analyzeInputConstraints(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        process.getFreeVariables().stream().forEach(v -> {
            analyzeInputConstraints(v, dmns, info);
        });
    }

}
