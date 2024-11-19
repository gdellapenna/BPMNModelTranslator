
package dellapenna.personal.bpmn.bpmn;

/**
 *
 * @author giuse
 */
public class Options {
    private boolean debug=false;
    private boolean trueParallel=false;
    private String inputsFile = "inputs.properties";
    private String outputsFile = "outputs.properties";

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the inputsFile
     */
    public String getInputsFile() {
        return inputsFile;
    }

    /**
     * @param inputsFile the inputsFile to set
     */
    public void setInputsFile(String inputsFile) {
        this.inputsFile = inputsFile;
    }

    /**
     * @return the outputsFile
     */
    public String getOutputsFile() {
        return outputsFile;
    }

    /**
     * @param outputsFile the outputsFile to set
     */
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
