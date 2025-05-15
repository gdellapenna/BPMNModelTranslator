package dellapenna.personal.bpmn.bpmn;

import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author Giuseppe Della Penna
 */
public record BPMNDecodedNode(Code code, FlowNode nextStep) {

}
