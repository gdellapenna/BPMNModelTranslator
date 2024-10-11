package dellapenna.personal.bpmn.bpmn;

import java.util.List;

/**
 *
 * @author giuse
 */
public record BPMNDecoded<T>(List<BPMNDecodedProcess<T>> processes) {
    
}
