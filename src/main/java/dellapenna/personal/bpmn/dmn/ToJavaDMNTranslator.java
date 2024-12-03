package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslator;
import dellapenna.personal.bpmn.feel.FeelTranslator.FeelTranslationInfo;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.Output;

public class ToJavaDMNTranslator extends AbstractDMNTranslator<String> {

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    @Override
    public String translateExpression(String input, String exp, DMNTranslationInfo info) throws FeelTranslatorException {
        FeelTranslationInfo f_info = new FeelTranslator.FeelTranslationInfo();
        String translation = feel.translate(input, exp, f_info);
        info.getReadVariables().addAll(f_info.getUsedVariableNames());
        return translation;
    }

    @Override
    public String generateRulesSource(List<DMNDecisionRule<String>> decoded_rules, String output_record_name, DMNTranslationInfo info) {
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

    @Override
    protected String generateDecisionTableSource(String id, List<Input> inputs, List<Output> outputs, List<DMNDecisionRule<String>> decoded_rules, DMNTranslationInfo info) {
        String procName = sanitizeName("dmn_dtable_" + id);
        String output_record_name = procName + "_result";

        return "// wrapper class for the output of DMN table " + id + "\n"
                + "class " + output_record_name + "{"
                + outputs.stream()
                        .map(o -> mapType(o.getTypeRef()) + " " + o.getName())
                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "public " + output_record_name + "("
                + outputs.stream()
                        .map(o -> mapType(o.getTypeRef()) + " " + o.getName())
                        .collect(Collectors.joining(","))
                + ") {"
                + outputs.stream()
                        .map(o -> "this." + o.getName() + "=" + o.getName())
                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "}\n"
                + "public String toString() { String result=\"{\"; "
                + outputs.stream()
                        .map(o -> "result+=\"" + o.getName() + "=\"+this." + o.getName() + " ")
                        .collect(Collectors.joining(";\n", "", ";\n"))
                + "return result+\"}\";"
                + "}\n"
                + "}"
                + "\n\n"
                + "// decision code for DMN table " + id + "\n"
                + "class " + procName + " {\n\n"
                //+ "public " + output_record_name + " " + procName + "("
                + "public static " + output_record_name + " execute("
                //                + inputs.stream()
                //                        .map(i -> mapType(i.getInputExpression().getTypeRef()) + " " + sanitizeName(i.getInputExpression().getTextContent()))
                //                        .collect(Collectors.joining(", "))

                + inputs.stream()
                        .map(i -> "Object _" + sanitizeName(i.getInputExpression().getTextContent()))
                        .collect(Collectors.joining(", "))
                + ") {\n\n"
                + inputs.stream()
                        .map(i -> mapType(i.getInputExpression().getTypeRef()) + " " + sanitizeName(i.getInputExpression().getTextContent())
                        + " = BPMNExecTypeUtils.to" + i.getInputExpression().getTypeRef() + "(_" + sanitizeName(i.getInputExpression().getTextContent()) + ")")
                        .collect(Collectors.joining(";\n", "", ";\n")) + "\n"
                + generateRulesSource(decoded_rules, output_record_name, info)
                + "\n}"
                + "\n}";

    }

    @Override
    protected String generateDecisionModelSource(Map<String, String> decoded_tables, DMNTranslationInfo info) {
        return """
               /*
                * ****************************** DMN Generated Code *************************
                */
               """
                + decoded_tables.values().stream().collect(Collectors.joining("\n\n"));
    }

    @Override
    public String mapType(String typeRef) {
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
}
