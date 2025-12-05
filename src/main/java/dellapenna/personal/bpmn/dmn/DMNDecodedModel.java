package dellapenna.personal.bpmn.dmn;

import java.util.Map;

/**
 *
 * @author Giuseppe Della Penna
 */
public record DMNDecodedModel<T>(String id, Map<String, DMNDecodedTable<T>> tables) {

}
