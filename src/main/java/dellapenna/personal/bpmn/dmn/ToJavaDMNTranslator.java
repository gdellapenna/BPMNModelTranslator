package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.List;
import java.util.stream.Collectors;

public class ToJavaDMNTranslator extends AbstractDMNTranslator<String> {

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    private String mapType(String typeRef) {
        return switch (typeRef.toLowerCase()) {
            case "number" ->
                "Double";
            case "string" ->
                "String";
            case "boolean" ->
                "Boolean";
            default ->
                "Object";
        };
    }

    private String generateRulesSource(List<DMNDecodedRule<String>> decoded_rules, String output_record_name, DMNTranslationInfo info) {
        return decoded_rules.stream().map(r -> {
            String cond = r.conditions().stream().map(c -> (c.testExpression())).filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(" && "));
            String rs = "";
            if (!cond.isBlank()) {
                rs += "if (" + cond + ")";
            }
            rs += " { return new " + output_record_name + "("
                    //+ r.assignments().stream().map(a -> (a.outputName() + " = " + a.outputExpression())).collect(Collectors.joining("; "))
                    + r.assignments().stream().map(a -> ("/*" + a.outputName() + "*/" + a.outputExpression())).collect(Collectors.joining(", "))
                    + ");}";

            return rs;
        }).collect(Collectors.joining(" else "));
    }

    private String generateDecisionTableSource(DMNDecodedTable<String> table, DMNTranslationInfo info) {
        String tableClassName = sanitizeName("dmn_dtable_" + table.id());
        String resultClassName = tableClassName + "_result";
        String argumentsClassName = tableClassName + "_arguments";

        return "// wrapper class for the output of DMN table " + table.id() + "\n"
                + "class " + resultClassName + "{"
                + table.outputs().stream()
                        .map(o -> mapType(o.getValue()) + " " + o.getKey())
                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "public " + resultClassName + "("
                + table.outputs().stream()
                        .map(o -> mapType(o.getValue()) + " " + o.getKey())
                        .collect(Collectors.joining(","))
                + ") {"
                + table.outputs().stream()
                        .map(o -> "this." + o.getKey() + "=" + o.getKey())
                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "}\n"
                + "public String toString() { String result=\"{\"; "
                + table.outputs().stream()
                        .map(o -> "result+=\"" + o.getKey() + "=\"+this." + o.getKey() + " ")
                        .collect(Collectors.joining(";\n", "", ";\n"))
                //                + table.outputs().stream()
                //                        .map(o -> mapType(o.getTypeRef()) + " " + o.getName())
                //                        .collect(Collectors.joining(";\n", "", ";\n"))
                //                + "public " + resultClassName + "("
                //                + outputs.stream()
                //                        .map(o -> mapType(o.getTypeRef()) + " " + o.getName())
                //                        .collect(Collectors.joining(","))
                //                + ") {"
                //                + outputs.stream()
                //                        .map(o -> "this." + o.getName() + "=" + o.getName())
                //                        .collect(Collectors.joining(";\n", "", ";\n"))
                //                + "}\n"
                //                + "public String toString() { String result=\"{\"; "
                //                + outputs.stream()
                //                        .map(o -> "result+=\"" + o.getName() + "=\"+this." + o.getName() + " ")
                //                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "return result+\"}\";"
                + "}\n"
                + "}"
                + "\n\n"
                //
                + "// wrapper class for the input of DMN table " + table.id() + "\n"
                + "class " + argumentsClassName + "{"
                + table.inputs().stream()
                        .map(i -> "public Object" /*+ mapType(i.getInputExpression().getTypeRef())*/ + " " + sanitizeName(i.getKey()))
                        .collect(Collectors.joining(";\n", "", ";\n"))
                //                + inputs.stream()
                //                        .map(i -> "public Object" /*+ mapType(i.getInputExpression().getTypeRef())*/ + " " + sanitizeName(i.getInputExpression().getTextContent()))
                //                        .collect(Collectors.joining(";\n", "", ";\n"))                
                + "}"
                + "\n\n"
                //
                + "// decision code for DMN table " + table.id() + "\n"
                + "class " + tableClassName + " {\n\n"
                //+ "public " + output_record_name + " " + procName + "("
                + "public static " + resultClassName + " execute("
                + argumentsClassName + " args"
                /*
                + inputs.stream()
                        .map(i -> "Object " + sanitizeName(i.getInputExpression().getTextContent()))
                        .collect(Collectors.joining(", "))
                 */
                + ") {\n\n"
                + table.inputs().stream()
                        .map(i -> /*mapType(i.getInputExpression().getTypeRef())*/ "Object" + " " + sanitizeName(i.getKey()) + " = args." + sanitizeName(i.getKey()))
                        .collect(Collectors.joining(";\n", "", ";\n")) + "\n"
                /*+ inputs.stream()
                        .map(i -> mapType(i.getInputExpression().getTypeRef()) + " " + sanitizeName(i.getInputExpression().getTextContent())
                        + " = BPMNExecTypeUtils.to" + i.getInputExpression().getTypeRef() + "(_" + sanitizeName(i.getInputExpression().getTextContent()) + ")")
                        .collect(Collectors.joining(";\n", "", ";\n")) + "\n"*/
                + generateRulesSource(table.rules(), resultClassName, info)
                + "\n}"
                + "\n}";

    }

    @Override
    public String translateExpression(String input, String exp, DMNTranslationInfo info) throws FeelTranslatorException {
        FeelTranslationInfo f_info = new FeelTranslationInfo();
        String translation = feel.translate(input, exp, f_info);
        info.getReadVariables().addAll(f_info.getUsedVariableNames());
        return translation;
    }

    @Override
    public String generateDecisionModelSource(DMNDecodedModel<String> model, DMNTranslationInfo info) {
        return """
               /*
                * ****************************** DMN Generated Code *************************
                */
               """
                + model.tables().values().stream().map(t -> generateDecisionTableSource(t, info)).collect(Collectors.joining("\n\n"));
    }

}
