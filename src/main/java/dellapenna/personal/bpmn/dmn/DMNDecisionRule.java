package dellapenna.personal.bpmn.dmn;

import java.util.List;

/**
 *
 * @author giuse
 */
public record DMNDecisionRule<T>(List<DMNCondition<T>> conditions, List<DMNAssignment<T>> assignments, String comment) {

}
