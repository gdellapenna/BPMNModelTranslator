package dellapenna.personal.bpmn.dmn;

import java.util.List;
import java.util.Map;

/**
 *
 * @author giuse
 */
public record DMNDecisionTable<T>(String id, List<DMNDecisionRule<T>> rules, Map<String, String> inputs, Map<String, String> outputs) {

}
