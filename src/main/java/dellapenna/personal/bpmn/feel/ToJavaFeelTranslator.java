package dellapenna.personal.bpmn.feel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author giuse
 */
public class ToJavaFeelTranslator extends AbstractFeelTranslator<String> {

    private String castToNumber(String a) {
        return "BPMNExecTypeUtils.tonumber(" + a + ")";
    }

    private String castToString(String a) {
        return "BPMNExecTypeUtils.tostring(" + a + ")";
    }

    private String castToBoolean(String a) {
        return "BPMNExecTypeUtils.toboolean(" + a + ")";
    }

    @Override
    public String translateGreaterThan(String arg1, String arg2, FeelTranslationInfo info) {
        return ((arg1 != null ? castToNumber(arg1) : "") + " > " + castToNumber(arg2));
    }

    @Override
    public String translateLessThan(String arg1, String arg2, FeelTranslationInfo info) {
        return ((arg1 != null ? castToNumber(arg1) : "") + " < " + castToNumber(arg2));
    }

    @Override
    public String translateGreaterOrEqual(String arg1, String arg2, FeelTranslationInfo info) {
        return ((arg1 != null ? castToNumber(arg1) : "") + " >= " + castToNumber(arg2));
    }

    @Override
    public String translateLessOrEqual(String arg1, String arg2, FeelTranslationInfo info) {
        return ((arg1 != null ? castToNumber(arg1) : "") + " <= " + castToNumber(arg2));
    }

    @Override
    public String translateEqual(String arg1, String arg2, FeelTranslationInfo info) {
        return "BPMNExecTypeUtils.equals(" + ((arg1 != null ? arg1 : "") + "," + arg2 + ")");
        //return ((arg1 != null ? arg1 : "") + ".equals(" + arg2 + ")");
    }

    @Override
    public String translateNot(String arg1, FeelTranslationInfo info) {
        return ("!(" + castToBoolean(arg1) + ")");
    }

    @Override
    public String translateAnd(String arg1, String arg2, FeelTranslationInfo info) {
        return (castToBoolean(arg1) + " && " + castToBoolean(arg2));
    }

    @Override
    public String translateOr(String arg1, String arg2, FeelTranslationInfo info) {
        return ("(" + castToBoolean(arg1) + " || " + castToBoolean(arg2) + ")");
    }

    @Override
    public String translateNumber(String context, BigDecimal value, FeelTranslationInfo info) {
        if (context != null) {
            return castToNumber(context) + ".equals(" + String.valueOf(value.doubleValue()) + ")";
        } else {
            return String.valueOf(value.doubleValue());
        }

    }

    @Override
    public String translateBoolean(String context, boolean value, FeelTranslationInfo info) {
        if (context != null) {
            return castToBoolean(context) + ".equals(" + (value ? "true" : "false") + ")";
        } else {
            return value ? "true" : "false";
        }

    }

    @Override
    public String translateString(String context, String value, FeelTranslationInfo info) {
        if (context != null) {
            return castToString(context) + ".equals(" + "\"" + value + "\")";
        } else {
            return "\"" + value + "\"";
        }

    }

    @Override
    public String translateAddition(String arg1, String arg2, FeelTranslationInfo info) {
        return ("(" + castToNumber(arg1) + " + " + castToNumber(arg2) + ")");
    }

    @Override
    public String translateSubtraction(String arg1, String arg2, FeelTranslationInfo info) {
        return ("(" + castToNumber(arg1) + " - " + castToNumber(arg2) + ")");
    }

    @Override
    public String translateDivision(String arg1, String arg2, FeelTranslationInfo info) {
        return (castToNumber(arg1) + " / " + castToNumber(arg2));
    }

    @Override
    public String translateMultiplication(String arg1, String arg2, FeelTranslationInfo info) {
        return (castToNumber(arg1) + " * " + castToNumber(arg2));
    }

    @Override
    public String translateNegation(String arg1, FeelTranslationInfo info) {
        return ("-(" + castToNumber(arg1) + ")");
    }

    @Override
    public String translateExponentiation(String arg1, String arg2, FeelTranslationInfo info) {
        return ("Math.pow(" + castToNumber(arg1) + "," + castToNumber(arg2) + ")");
    }

    @Override
    public String translateFunctionCall(String function, List<String> arguments, FeelTranslationInfo info) {
        //TODO: add translation for other meaningful FEEL functions
        return switch (function) {
            case "length" ->
                castToString(arguments.get(0)) + ".length()"; //assuming the correctness of the FEEL function arguments
            case "abs" ->
                "Math.abs(" + castToNumber(arguments.get(0)) + ")"; //assuming the correctness of the FEEL function arguments
            default ->
                function + "(" + arguments.stream().collect(Collectors.joining(",")) + ")";
        };
    }

    @Override
    public String translateInTest(String arg1, String arg2, FeelTranslationInfo info) {
        if (arg1 != null) {
            return "contains(" + arg1 + "," + arg2 + ")";
        } else {
            return "contains(" + arg2 + ")"; //!!!!
        }
    }

    @Override
    protected String translateInTest(String arg1, boolean startOpen, String start, boolean endOpen, String end, FeelTranslationInfo info) {
        return "(" + castToNumber(arg1) + (startOpen ? ">" : ">=") + start + " && " + castToNumber(arg1) + (endOpen ? "<" : "<=") + end + ")";
    }

    @Override
    public String translateConstRange(String arg1, String arg2, FeelTranslationInfo info) {
        return "constRange(" + arg1 + "," + arg2 + ")";
    }

    @Override
    public String translateVariableReference(List<String> names, FeelTranslationInfo info) {
        if (info != null) {
            info.getUsedVariableNames().add(names);
        }
        return names.stream().collect(Collectors.joining("."));
    }

    @Override
    protected String translateConstList(String context, List<String> list, FeelTranslationInfo info) {
        if (context != null) {
            return "inList(" + context + "," + list.stream().collect(Collectors.joining(",")) + ")";
        } else {
            return list.stream().collect(Collectors.joining(","));
        }

    }

    @Override
    protected String translatePath(String key, String path, FeelTranslationInfo info) {
        return key + "." + path;
    }

}
