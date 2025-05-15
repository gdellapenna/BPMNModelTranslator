package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.BDTransException;

/**
 *
 * @author Giuseppe Della Penna
 */
public class BpmnTranslatorException extends BDTransException {

    public BpmnTranslatorException(String message) {
        super(message);
    }

    public BpmnTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
