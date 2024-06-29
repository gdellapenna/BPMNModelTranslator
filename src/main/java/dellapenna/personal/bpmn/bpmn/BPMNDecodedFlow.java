package dellapenna.personal.bpmn.bpmn;

import java.util.List;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record BPMNDecodedFlow<T>(String name, List<T> code, FlowNode firstStep) {

}
