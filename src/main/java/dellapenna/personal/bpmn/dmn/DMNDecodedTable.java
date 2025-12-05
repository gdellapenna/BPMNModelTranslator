package dellapenna.personal.bpmn.dmn;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public record DMNDecodedTable<T>(String id, List<DMNDecodedRule<T>> rules, List<SimpleImmutableEntry<String, String>> inputs, List<SimpleImmutableEntry<String, String>> outputs) {

}
