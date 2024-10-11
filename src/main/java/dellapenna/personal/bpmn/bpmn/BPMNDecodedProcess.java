package dellapenna.personal.bpmn.bpmn;

import java.util.List;

/**
 *
 * @author giuse
 */
public record BPMNDecodedProcess<T>(String name, List<BPMNDecodedFlow<T>> body) {
    
}
