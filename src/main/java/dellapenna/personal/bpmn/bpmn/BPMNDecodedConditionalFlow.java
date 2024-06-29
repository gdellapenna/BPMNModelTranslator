package dellapenna.personal.bpmn.bpmn;

import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record BPMNDecodedConditionalFlow<T>(String name, String condition, FlowNode firstStep) {

}
