package dellapenna.personal.bpmn.bpmn;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Giuseppe Della Penna
 */
public record FunctionDefinition<T>(String name, Code body, List<T> triggers, String returnType, Map<String,String> parameters) {
    
}
