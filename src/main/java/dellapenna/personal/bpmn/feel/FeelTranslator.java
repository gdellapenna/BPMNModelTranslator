package dellapenna.personal.bpmn.feel;

import org.camunda.feel.syntaxtree.Exp;

/**
 *
 * @author Giuseppe Della Penna
 * @param <T>
 */
public interface FeelTranslator<T> {

    T translate(String expression, FeelTranslationInfo info) throws FeelTranslatorException;

    T translateChecked(String expression, FeelTranslationInfo info);

    T translate(String input, String expression, FeelTranslationInfo info) throws FeelTranslatorException;

    T translateChecked(String input, String expression, FeelTranslationInfo info);

    Exp parse(String expression) throws FeelTranslatorException;

}
