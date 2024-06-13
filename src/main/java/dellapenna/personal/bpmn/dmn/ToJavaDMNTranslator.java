package dellapenna.personal.bpmn.dmn;

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
    public String translateExpression(String input, String exp) throws FeelTranslatorException {
        return feel.translate(input, exp);
    }

    @Override
    public String translateRules(List<DMNDecisionRule<String>> decoded_rules, String output_record_name) {
        return decoded_rules.stream().map(r
                -> ("if ("
                + r.conditions().stream().map(c -> (c.testExpression())).collect(Collectors.joining(" && "))
                + ") { return new " + output_record_name + "("
                //+ r.assignments().stream().map(a -> (a.outputName() + " = " + a.outputExpression())).collect(Collectors.joining("; "))
                + r.assignments().stream().map(a -> ("/*" + a.outputName() + "*/" + a.outputExpression())).collect(Collectors.joining(", "))
                + ");}")
        ).collect(Collectors.joining(" else "));
    }

    @Override
    protected String translateDecisionTable(String id, List<Input> inputs, List<Output> outputs, List<DMNDecisionRule<String>> decoded_rules) {
        String procName = sanitizeName("dmn_" + id);
        String output_record_name = procName + "_result";

        return "public static record " + output_record_name + "("
                + outputs.stream()
                        .map(o -> o.getTypeRef() + " " + o.getName())
                        .collect(Collectors.joining(", "))
                + "){}"
                + "\n\n"
                + "public " + output_record_name + " " + procName + "("
                + inputs.stream()
                        .map(i -> i.getInputExpression().getTypeRef() + " " + sanitizeName(i.getInputExpression().getTextContent()))
                        .collect(Collectors.joining(", "))
                + ") {\n\n" + translateRules(decoded_rules, output_record_name)
                + "\n}";

//                return "public Map<String,Object> " + sanitizeName("dmn_" + id) + "(Map<String,Object> params) {\n"
//                + inputs.stream()
//                        .map(i -> "var " + sanitizeName(i.getInputExpression().getTextContent()) + " = params.get(\"" + sanitizeName(i.getInputExpression().getTextContent()) + "\");"
//                        + "//type: " + i.getInputExpression().getTypeRef())
//                        .collect(Collectors.joining("\n"))
//                + "\n\n" + translated_rules
//                + "\n\n\tMap<String,Object> result = new HashMap<>();\n"
//                + outputs.stream()
//                        .map(o -> "\tresult.put(\"" + o.getName() + "\",VALUE); //type: " + o.getTypeRef())
//                        .collect(Collectors.joining("\n"))
//                + "\nreturn result;"
//                + "\n}";
    }

    @Override
    protected String translateDecisionModel(Map<String, String> decoded_tables) {
        return decoded_tables.values().stream().collect(Collectors.joining("\n\n"));
    }

    @Override
    protected String getTrueExpression() {
        return "true";
    }
}
