package dellapenna.personal.bpmn;

/**
 *
 * @author Giuseppe Della Penna
 */
public class BDTransException extends Exception {

    public BDTransException(String message) {
        super(message);
    }

    public BDTransException(String message, Throwable cause) {
        super(message, cause);
    }

}
