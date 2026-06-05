package dellapenna.personal.bpmn.feel;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Giuseppe Della Penna
 */
public class ToJavaFeelTranslator extends AbstractFeelTranslator<String> {

    private String castToContext(String a) {
        return "BPMNExecTypeUtils.tocontext(" + a + ")";
    }

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
        List<String> excludeGetters;
        int maxGetterslevel;
        boolean generateReadNextGetters;
        if (info != null) {
            info.getUsedVariableNames().add(names);
            excludeGetters = info.getExcludeGetters();
            maxGetterslevel = info.getMaxGettersLevel();
            generateReadNextGetters = info.isGenerateReadNextGetters();
        } else {
            excludeGetters = Collections.EMPTY_LIST;
            maxGetterslevel = 0;
            generateReadNextGetters = false;
        }

        String result = "";
        for (int i = 0; i < names.size(); ++i) {
            result += (result.isEmpty() ? "" : ".")
                    + ((excludeGetters.contains(names.get(i)) || i > (maxGetterslevel - 1))
                    ? names.get(i)
                    : ("get" + names.get(i).substring(0, 1).toUpperCase() + names.get(i).substring(1) + "(s," + (generateReadNextGetters ? "true" : "false") + ")"));
        }
        return result;
        //return names.stream().map(p -> (!generateGetters || excludeGetters.contains(p)) ? p : ("get" + p.substring(0, 1).toUpperCase() + p.substring(1) + "()")).collect(Collectors.joining("."));

        //andrebbe fatto per tutti gli i>0, quindi maxGettersLevel dovrebbe essere sempre 1!
        //i getter valgono solo per le variabili di base
        //ma per le strutture esplicite come quelle ritornate dalla DMN? Usiamo mappe anche in quel caso?
//        for (int i = 0; i < names.size(); ++i) {
//            if (i == 0) {
//                result = (excludeGetters.contains(names.get(i)) ? names.get(i) : ("get" + names.get(i).substring(0, 1).toUpperCase() + names.get(i).substring(1) + "(s," + (generateReadNextGetters ? "true" : "false") + ")"));
//            } else {
//                result = "castToContext(" + result + ").get(" + names.get(i) + ")";
//            }
//        }
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
