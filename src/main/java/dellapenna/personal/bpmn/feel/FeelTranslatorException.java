package dellapenna.personal.bpmn.feel;

import dellapenna.personal.bpmn.BDTransException;

/**
 *
 * @author Giuseppe Della Penna
 */
public class FeelTranslatorException extends BDTransException {

    public FeelTranslatorException(String message) {
        super(message);
    }

    public FeelTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
