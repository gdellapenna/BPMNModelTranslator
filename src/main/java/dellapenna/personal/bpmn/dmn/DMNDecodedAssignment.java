package dellapenna.personal.bpmn.dmn;

/**
 *
 * @author giuse
 */
public record DMNDecodedAssignment<T>(String outputName, T outputExpression, String sourceExpression) {

}
