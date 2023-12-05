package dellapenna.personal.bpmn.feel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author giuse
 */
public class ToJavaFeelTranslator extends AbstractFeelTranslator<String> {

    @Override
    public String translateGreaterThan(String arg1, String arg2) {
        return ((arg1 != null ? arg1 : "") + " > " + arg2);
    }

    @Override
    public String translateLessThan(String arg1, String arg2) {
        return ((arg1 != null ? arg1 : "") + " < " + arg2);
    }

    @Override
    public String translateGreaterOrEqual(String arg1, String arg2) {
        return ((arg1 != null ? arg1 : "") + " >= " + arg2);
    }

    @Override
    public String translateLessOrEqual(String arg1, String arg2) {
        return ((arg1 != null ? arg1 : "") + " <= " + arg2);
    }

    @Override
    public String translateEqual(String arg1, String arg2) {
        return ((arg1 != null ? arg1 : "") + " == " + arg2);
    }

    @Override
    public String translateNot(String arg1) {
        return ("!(" + arg1 + ")");
    }

    @Override
    public String translateAnd(String arg1, String arg2) {
        return (arg1 + " && " + arg2);
    }

    @Override
    public String translateOr(String arg1, String arg2) {
        return ("(" + arg1 + " || " + arg2 + ")");
    }

    @Override
    public String translateNumber(BigDecimal value) {
        return value.toString();
    }

    @Override
    public String translateBoolean(boolean value) {
        return value ? "true" : "false";
    }

    @Override
    public String translateString(String value) {
        return "\"" + value + "\"";

    }

    @Override
    public String translateAddition(String arg1, String arg2) {
        return ("(" + arg1 + " + " + arg2 + ")");
    }

    @Override
    public String translateSubtraction(String arg1, String arg2) {
        return ("(" + arg1 + " - " + arg2 + ")");
    }

    @Override
    public String translateDivision(String arg1, String arg2) {
        return (arg1 + " / " + arg2);
    }

    @Override
    public String translateMultiplication(String arg1, String arg2) {
        return (arg1 + " * " + arg2);
    }

    @Override
    public String translateNegation(String arg1) {
        return ("-(" + arg1 + ")");
    }

    @Override
    public String translateExponentiation(String arg1, String arg2) {
        return ("Math.pow(" + arg1 + "," + arg2 + ")");
    }

    @Override
    public String translateFunctionCall(String function, List<String> arguments) {
        return function + "(" + arguments.stream().collect(Collectors.joining(",")) + ")";
    }

    @Override
    public String translateInTest(String arg1, String arg2) {
        if (arg1 != null) {
            return "contains(" + arg1 + "," + arg2 + ")";
        } else {
            return "contains(" + arg2 + ")"; //!!!!
        }
    }

    @Override
    public String translateConstRange(String arg1, String arg2) {
        return "constRange(" + arg1 + "," + arg2 + ")";
    }

    @Override
    public String translateVariableReference(List<String> names) {
        return names.get(0); //???
    }

    @Override
    protected String translateConstList(List<String> list) {
        return list.stream().collect(Collectors.joining(",")); //!!!!
    }

}
