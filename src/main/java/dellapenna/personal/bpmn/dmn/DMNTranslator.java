/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dellapenna.personal.bpmn.dmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DecisionTable;

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
