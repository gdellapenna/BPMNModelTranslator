package dellapenna.personal.bpmnmodeltest;

import java.util.List;

/**
 *
 * @author giuse
 */
public record DecisionRule<T>(List<Condition<T>> conditions, List<Assignment<T>> assignments, String comment) {

}
