package dellapenna.personal.bpmn.bpmn;

import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record BPMNDecodedFlow<T>(String name, T code, FlowNode firstStep, FlowNode lastStep) {

}
