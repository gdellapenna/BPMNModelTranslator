package dellapenna.personal.bpmn.feel;

//import org.camunda.feel.syntaxtree.Exp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface FeelTranslator<T> {

    public static class FeelTranslationInfo {

        private final List<List<String>> usedVariableNames = new ArrayList<>();

        public List<List<String>> getUsedVariableNames() {
            return usedVariableNames;
        }
    };

    T translate(String expression, FeelTranslationInfo info) throws FeelTranslatorException;

    T translateChecked(String expression, FeelTranslationInfo info);

    T translate(String input, String expression, FeelTranslationInfo info) throws FeelTranslatorException;

    T translateChecked(String input, String expression, FeelTranslationInfo info);

//    T translateExp(Exp e) throws FeelTranslatorException;
//
//    T translateExp(Exp input, Exp e) throws FeelTranslatorException;
}
