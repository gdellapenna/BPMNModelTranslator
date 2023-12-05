package dellapenna.personal.bpmnmodeltest;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 *
 * @author giuse
 */
interface BPMNTranslator<T> {
     
    public T translate(BpmnModelInstance dmn) throws FeelTranslatorException;
}
