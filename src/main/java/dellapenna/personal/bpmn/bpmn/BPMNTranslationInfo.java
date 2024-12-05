package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giuse
 */
public class BPMNTranslationInfo {

    private boolean debug;
    private boolean trueParallel;
    private String inputsFile;
    private String outputsFile;

    private final List<VariableDefinition> detectedInputVariables = new ArrayList<>();

    public BPMNTranslationInfo() {
        debug = false;
        trueParallel = false;
        inputsFile = "inputs.properties";
        outputsFile = "outputs.properties";
    }

    public BPMNTranslationInfo(BPMNTranslationInfo other) {
        //            this.readVariables.addAll(other.readVariables);
        //            this.writtenVariables.addAll(other.writtenVariables);
        this.debug = other.debug;
        this.trueParallel = other.trueParallel;
        this.inputsFile = other.inputsFile;
        this.outputsFile = other.outputsFile;
    } //            this.readVariables.addAll(other.readVariables);
    //            this.writtenVariables.addAll(other.writtenVariables);

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

    public List<VariableDefinition> getDetectedInputVariables() {
        return detectedInputVariables;
    }

}
