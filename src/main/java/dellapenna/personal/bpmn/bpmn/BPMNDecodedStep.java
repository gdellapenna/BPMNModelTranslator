package dellapenna.personal.bpmn.bpmn;

import java.util.List;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record BPMNDecodedStep<T>(List<T> code, FlowNode nextStep) {

}
