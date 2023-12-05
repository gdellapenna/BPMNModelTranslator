package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.List;
import java.util.stream.Collectors;

public class ToJavaDMNTranslator extends AbstractDMNTranslator<String> {

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    @Override
    public String translateExpression(String input, String exp) throws FeelTranslatorException {
        return feel.translate(input, exp);
    }

    @Override
    public String translateRules(List<DMNDecisionRule<String>> decoded_rules) {
        return decoded_rules.stream().map(r
                -> ("if ("
                + r.conditions().stream().map(c -> (c.testExpression())).collect(Collectors.joining(" && "))
                + ") {"
                + r.assignments().stream().map(a -> (a.outputName() + " = " + a.outputExpression())).collect(Collectors.joining("; "))
                + "}")
        ).collect(Collectors.joining(" else "));
    }

}
