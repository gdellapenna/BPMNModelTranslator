package dellapenna.personal.bpmn.dmn;

/**
 *
 * @author Giuseppe Della Penna
 */
public record DMNDecodedAssignment<T>(String outputName, T outputExpression, String sourceExpression) {

}
