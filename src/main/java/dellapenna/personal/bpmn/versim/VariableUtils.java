package dellapenna.personal.bpmn.versim;

import dellapenna.personal.util.OutputManager;
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
import java.util.stream.Stream;
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
import org.camunda.feel.syntaxtree.Comparison;
import org.camunda.feel.syntaxtree.ConstBool;
import org.camunda.feel.syntaxtree.Equal;
import org.camunda.feel.syntaxtree.GreaterOrEqual;
import org.camunda.feel.syntaxtree.GreaterThan;
import org.camunda.feel.syntaxtree.LessOrEqual;
import org.camunda.feel.syntaxtree.LessThan;
import org.camunda.feel.syntaxtree.Ref;

/**
 *
 * @author Giuseppe Della Penna
 */
public class VariableUtils {

    FeelTranslator<String> ft = new ToJavaFeelTranslator();

    public void decodeDMNVariableCostraint(String condition_expression, VariableDefinition.VariableBounds b) throws FeelTranslatorException {
         if (condition_expression.startsWith("#TYPE:")) {
            b.setTypeHint(condition_expression.substring(6).trim());
        } else if (!condition_expression.isBlank()) {
            Exp exp = ft.parse(condition_expression);
            switch (exp) {
                case ConstString texp ->
                    b.addCase(texp.value());
                case ConstNumber texp ->
                    b.updateRange(texp.value().bigDecimal().doubleValue());
                case ConstBool texp ->
                    b.addCase(texp.value() ? "true" : "false");
                case InputInRange texp -> {
                    ConstRangeBoundary left = texp.range().start();
                    ConstRangeBoundary right = texp.range().end();
                    //TODO handle open and close ranges
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
                    OutputManager.getInstance().emit(OutputManager.MessageType.WARNING, 2, "unhandled expression: " + condition_expression);
                }
            }
            //TODO handle other comparisons?
        } else {
            //currently don't care
        }
    }

    public boolean decodeBPMNVariableConstraint(Exp exp, VariableDefinition v) throws FeelTranslatorException {
        if (exp instanceof Comparison c) {
            boolean inverse = false;
            Exp compared_expression = null;
            if (c.x() instanceof Ref r && String.join(".", scala.collection.JavaConverters.asJava(r.names())).equals(v.getName())) {
                inverse = false;
                compared_expression = c.y();
            } else if (c.y() instanceof Ref r && String.join(".", scala.collection.JavaConverters.asJava(r.names())).equals(v.getName())) {
                inverse = true;
                compared_expression = c.x();
            } else {
                return false;
            }
            if (compared_expression != null) {
                switch (exp) {
                    case GreaterThan texp -> {
                        if (compared_expression instanceof ConstNumber n) {
                            if (!inverse) {
                                v.getBounds().updateMin(n.value().bigDecimal().doubleValue(), true);
                            } else {
                                v.getBounds().updateMax(n.value().bigDecimal().doubleValue(), true);
                            }
                        }
                    }
                    case LessThan texp -> {
                        if (compared_expression instanceof ConstNumber n) {
                            if (!inverse) {
                                v.getBounds().updateMax(n.value().bigDecimal().doubleValue(), true);
                            } else {
                                v.getBounds().updateMin(n.value().bigDecimal().doubleValue(), true);
                            }

                        }
                    }
                    case GreaterOrEqual texp -> {
                        if (compared_expression instanceof ConstNumber n) {
                            if (!inverse) {
                                v.getBounds().updateMin(n.value().bigDecimal().doubleValue(), false);
                            } else {
                                v.getBounds().updateMax(n.value().bigDecimal().doubleValue(), false);
                            }
                        }
                    }
                    case LessOrEqual texp -> {
                        if (compared_expression instanceof ConstNumber n) {
                            if (!inverse) {
                                v.getBounds().updateMax(n.value().bigDecimal().doubleValue(), false);
                            } else {
                                v.getBounds().updateMin(n.value().bigDecimal().doubleValue(), false);
                            }
                        }
                    }
                    case Equal texp -> {
                        if (compared_expression instanceof ConstNumber n) {
                            v.getBounds().updateRange(n.value().bigDecimal().doubleValue());
                        } else if (compared_expression instanceof ConstString n) {
                            v.getBounds().addCase(n.value());
                        } else if (compared_expression instanceof ConstBool n) {
                            v.getBounds().addCase(n.value() ? "true" : "false");
                        } else {
                            return false;
                        }
                    }
                    default -> {
                        return false;
                    }
                }
            }
        } else if (exp instanceof org.camunda.feel.syntaxtree.Conjunction log) {
            boolean a = decodeBPMNVariableConstraint(log.x(), v);
            boolean b = decodeBPMNVariableConstraint(log.y(), v);
            return (a || b);
        } else if (exp instanceof org.camunda.feel.syntaxtree.Disjunction log) {
            boolean a = decodeBPMNVariableConstraint(log.x(), v);
            boolean b = decodeBPMNVariableConstraint(log.y(), v);
            return (a || b);
        } else if (exp instanceof org.camunda.feel.syntaxtree.Not log) {
            return decodeBPMNVariableConstraint(log.x(), v);
        } else {
            return false;
        }
        return true;
    }

    public void decodeBPMNVariableConstraint(String variable_expression, VariableDefinition v) throws FeelTranslatorException {
        if (variable_expression != null && !variable_expression.isBlank()) {
            Exp exp = ft.parse(variable_expression);
            if (!decodeBPMNVariableConstraint(exp, v)) {
                v.getBounds().addExpression(variable_expression); //not auto-deducible  
                OutputManager.getInstance().emit(OutputManager.MessageType.WARNING, 2, "unhandled expression: " + variable_expression);
            }
        }
    }

    public List<String> extractDMNConstraints(DMNDecodedModel<String>[] dmns, String table_id, String input_name) {
        DMNDecodedTable<String> table = Arrays.stream(dmns).flatMap(dmn -> dmn.tables().values().stream()).filter(t -> t.id().equals(table_id)).findFirst().orElse(null);
        if (table != null) {
            String dmn_declared_type = table.inputs().stream().filter(i -> i.getKey().equals(input_name)).map(i -> i.getValue()).findFirst().orElse("UNKNOWN");
            return Stream.concat(
                    table.rules().stream().flatMap(r -> r.conditions().stream()).filter(c -> c.inputExpression().equals(input_name)).map(c -> c.sourceTestExpression()),
                    Stream.of("#TYPE:" + dmn_declared_type)
            ).toList();            
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public VariableDefinition.VariableBounds analyzeInputConstraints(VariableDefinition v, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        v.getUsages(BPMNDecodedProcess.VariableDirection.READ).stream().forEach(u -> {
            if (v.getName().equals(u.sourceExpression())) {
                if (u.sourceId().startsWith("$DMN$")) {
                    String[] parts = u.sourceId().substring(5).split("\\$");
                    List<String> condition_expressions = extractDMNConstraints(dmns, parts[0], parts[1]);
                    for (String c : condition_expressions) {
                        try {
                            decodeDMNVariableCostraint(c, v.getBounds());
                        } catch (FeelTranslatorException ex) {
                            Logger.getLogger(VariableUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {
                try {
                    decodeBPMNVariableConstraint(u.sourceExpression(), v);
                } catch (FeelTranslatorException ex) {
                    Logger.getLogger(VariableUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return v.getBounds();
    }
}
