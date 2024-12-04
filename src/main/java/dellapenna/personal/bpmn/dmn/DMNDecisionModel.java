package dellapenna.personal.bpmn.dmn;

import java.util.Map;

public record DMNDecisionModel<T>(String id, Map<String, DMNDecisionTable<T>> tables) {

}
