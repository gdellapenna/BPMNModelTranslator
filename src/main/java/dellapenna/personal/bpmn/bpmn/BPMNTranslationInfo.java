package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.versim.Assertion;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public class BPMNTranslationInfo {

    private boolean debug;
    private boolean trace;
    private boolean trueParallel;
    private boolean dummyWorkload;
    private String inputsFile;
    private String outputsFile;
    private List<Assertion> globalAssertions;
    private List<String> forcedInputVariables;

    public BPMNTranslationInfo() {
        debug = false;
        trace = true;
        trueParallel = true;
        dummyWorkload = false;
        inputsFile = "inputs.properties";
        outputsFile = "outputs.properties";
        globalAssertions = new ArrayList<>();
        forcedInputVariables = new ArrayList<>();
    }

    public BPMNTranslationInfo(BPMNTranslationInfo other) {
        this();
        this.debug = other.debug;
        this.trace = other.trace;
        this.trueParallel = other.trueParallel;
        this.dummyWorkload = other.dummyWorkload;
        this.inputsFile = other.inputsFile;
        this.outputsFile = other.outputsFile;
        this.globalAssertions = new ArrayList<>(other.globalAssertions);
        this.forcedInputVariables = new ArrayList<>(other.forcedInputVariables);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
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

    public boolean isDummyWorkload() {
        return dummyWorkload;
    }

    public void setDummyWorkload(boolean dummyWorkload) {
        this.dummyWorkload = dummyWorkload;
    }

    public void addGlobalAssertion(String expression, String description) {
        globalAssertions.add(new Assertion(expression, description));
    }

    public List<Assertion> getGlobalAssertions() {
        return globalAssertions;
    }

    public void addForcedInputVariable(String variable) {
        forcedInputVariables.add(variable);
    }

    public List<String> getForcedInputVariables() {
        return forcedInputVariables;
    }

}
