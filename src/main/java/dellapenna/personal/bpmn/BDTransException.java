package dellapenna.personal.bpmn;

/**
 *
 * @author giuse
 */
public class BDTransException extends Exception {

    public BDTransException(String message) {
        super(message);
    }

    public BDTransException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
