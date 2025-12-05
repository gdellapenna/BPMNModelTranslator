package dellapenna.personal.bpmn.dmn;

import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public record DMNDecodedRule<T>(List<DMNDecodedCondition<T>> conditions, List<DMNDecodedAssignment<T>> assignments, String comment) {

}
