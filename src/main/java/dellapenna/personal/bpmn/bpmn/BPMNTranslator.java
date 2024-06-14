package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 *
 * @author giuse
 */
public interface BPMNTranslator<T> {
     
    public T translate(BpmnModelInstance dmn) throws FeelTranslatorException;
    
}
