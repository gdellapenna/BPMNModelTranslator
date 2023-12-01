package dellapenna.personal.bpmnmodeltest;

import org.camunda.feel.syntaxtree.Exp;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface FeelTranslator<T> {

    T translate(String expression) throws FeelTranslatorException;
    
    T translate(String input, String expression) throws FeelTranslatorException;

    T translateExp(Exp e) throws FeelTranslatorException;
    
    T translateExp(Exp input, Exp e) throws FeelTranslatorException;
    
}
