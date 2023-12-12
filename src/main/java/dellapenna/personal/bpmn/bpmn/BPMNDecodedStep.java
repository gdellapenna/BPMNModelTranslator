package dellapenna.personal.bpmn.bpmn;

import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record BPMNDecodedStep<T>(T code, FlowNode nextStep) {

}
