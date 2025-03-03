package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface BPMNTranslator<T> {


    T translate(BpmnModelInstance bpmn, DMNDecodedModel<T>[] dmns, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    T generateBpmnSource(BPMNDecodedModel bpmn, BPMNTranslationInfo info);

    BPMNDecodedModel decodeBpmn(BpmnModelInstance bpmn, DMNDecodedModel<T>[] dmns, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

}
