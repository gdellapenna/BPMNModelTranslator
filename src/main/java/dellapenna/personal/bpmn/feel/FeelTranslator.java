package dellapenna.personal.bpmn.feel;

import org.camunda.feel.syntaxtree.Exp;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface FeelTranslator<T> {

    void initTranslation();

    T translate(String expression) throws FeelTranslatorException;

    T translateChecked(String expression);

    T translate(String input, String expression) throws FeelTranslatorException;

    T translateChecked(String input, String expression);

    T translateExp(Exp e) throws FeelTranslatorException;

    T translateExp(Exp input, Exp e) throws FeelTranslatorException;

}
