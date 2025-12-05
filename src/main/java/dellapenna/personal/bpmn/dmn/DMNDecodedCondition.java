package dellapenna.personal.bpmn.dmn;

/**
 *
 * @author Giuseppe Della Penna
 */
public record DMNDecodedCondition<T>(T inputExpression, T testExpression, String sourceTestExpression) {

}
