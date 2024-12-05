package dellapenna.personal.bpmn.dmn;

import java.util.Map;

public record DMNDecodedModel<T>(String id, Map<String, DMNDecodedTable<T>> tables) {

}
