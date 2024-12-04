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

    public static class DMNTranslationInfo {

        private final List<List<String>> readVariables = new ArrayList<>();
        private final List<List<String>> writtenVariables = new ArrayList<>();

        public DMNTranslationInfo() {

        }

        public DMNTranslationInfo(DMNTranslationInfo other) {
            this.readVariables.addAll(other.readVariables);
            this.writtenVariables.addAll(other.writtenVariables);
        }

        public List<List<String>> getReadVariables() {
            return readVariables;
        }

        public List<List<String>> getWrittenVariables() {
            return writtenVariables;
        }
    };

    T translate(DmnModelInstance dmn, DMNTranslationInfo info) throws FeelTranslatorException;

    T generateDecisionModelSource(DMNDecisionModel<T> model, DMNTranslationInfo info);

    DMNDecisionModel<T> decodeDecisionModel(DmnModelInstance dmn, DMNTranslationInfo info) throws FeelTranslatorException;

}
