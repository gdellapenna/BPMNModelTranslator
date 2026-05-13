package dellapenna.personal.bpmn.bpmn;

import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author Giuseppe Della Penna
 */
public record BPMNDecodedBoundaryFlow(String name, BoundaryEvent event, FlowNode firstStep) {

}
