package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import org.camunda.bpm.model.dmn.DmnModelInstance;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface DMNTranslator<T> {


    T translate(DmnModelInstance dmn, DMNTranslationInfo info) throws FeelTranslatorException;

    T generateDecisionModelSource(DMNDecodedModel<T> model, DMNTranslationInfo info);
    
    T generateDecisionTableSource(DMNDecodedTable<T> table, DMNTranslationInfo info);

    DMNDecodedModel<T> decodeDecisionModel(DmnModelInstance dmn, DMNTranslationInfo info) throws FeelTranslatorException;

}
