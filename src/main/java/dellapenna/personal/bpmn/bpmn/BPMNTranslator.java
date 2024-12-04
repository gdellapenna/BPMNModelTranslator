package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.dmn.DMNDecisionModel;
import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface BPMNTranslator<T> {

    public static class BPMNTranslationInfo {

        private boolean debug;
        private boolean trueParallel;
        private String inputsFile;
        private String outputsFile;

//        private final List<List<String>> readVariables = new ArrayList<>();
//        private final List<List<String>> writtenVariables = new ArrayList<>();
        public BPMNTranslationInfo() {
            debug = false;
            trueParallel = false;
            inputsFile = "inputs.properties";
            outputsFile = "outputs.properties";
        }

        public BPMNTranslationInfo(BPMNTranslator.BPMNTranslationInfo other) {
//            this.readVariables.addAll(other.readVariables);
//            this.writtenVariables.addAll(other.writtenVariables);
            this.debug = other.debug;
            this.trueParallel = other.trueParallel;
            this.inputsFile = other.inputsFile;
            this.outputsFile = other.outputsFile;
        }

//        public List<List<String>> getReadVariables() {
//            return readVariables;
//        }
//
//        public List<List<String>> getWrittenVariables() {
//            return writtenVariables;
//        }
        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }

        public String getInputsFile() {
            return inputsFile;
        }

        public void setInputsFile(String inputsFile) {
            this.inputsFile = inputsFile;
        }

        public String getOutputsFile() {
            return outputsFile;
        }

        public void setOutputsFile(String outputsFile) {
            this.outputsFile = outputsFile;
        }

        public boolean isTrueParallel() {
            return trueParallel;
        }

        public void setTrueParallel(boolean trueParallel) {
            this.trueParallel = trueParallel;
        }

    }

    T translate(BpmnModelInstance bpmn, DMNDecisionModel<T>[] dmns, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    T generateBpmnSource(BPMNDecoded bpmn, BPMNTranslationInfo info);

    BPMNDecoded decodeBpmn(BpmnModelInstance bpmn, DMNDecisionModel<T>[] dmns, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

}
