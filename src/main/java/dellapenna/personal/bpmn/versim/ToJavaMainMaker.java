package dellapenna.personal.bpmn.versim;

import dellapenna.personal.bpmn.bpmn.BPMNDecoded;
import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import static dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator.sanitizeName;
import dellapenna.personal.bpmn.bpmn.VariableDefinition;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import java.util.stream.Collectors;

/**
 *
 * @author giuse
 */
public class ToJavaMainMaker {

    public String generateProcessLauncher(BPMNDecoded bpmn, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        String mainMethod = "public static void main(String[] args) {\n";
        if (info == null || !info.isDebug()) {
            mainMethod += "BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());";
        }
        if (info == null || info.isTrueParallel()) {
            mainMethod += "BPMNExecProcessUtils.enableTrueParallel();";
        }

        //test for first process only
        BPMNDecodedProcess process = bpmn.processes().get(0);
        mainMethod += "bpmn_process_" + sanitizeName(process.getName()) + " process = new " + "bpmn_process_" + sanitizeName(process.getName()) + "();\n";
        mainMethod += "process.execute("
                + process.getFreeVariables().stream().map(v -> "null" + "/*" + v.getName() + "*/").collect(Collectors.joining(","))
                + ");";

        mainMethod += "}";

        return " class Executor" + " { " + mainMethod + "}";
    }

    public String generateInputCostraintNotes(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        String result = """
                        
               /*
                * ****************************** Input Variable Constraints *************************
                */
               """;
        VariableUtils vu = new VariableUtils();
        for (VariableDefinition v : process.getFreeVariables()) {
            vu.analyzeInputConstraints(v, dmns, info);
            result += "//" + v.getName() + ":" + v.getBounds() + "\n";
        }
        return result;
    }

    public String generateInputProperties(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        String result = "";
        VariableUtils vu = new VariableUtils();
        for (VariableDefinition v : process.getFreeVariables()) {
            vu.analyzeInputConstraints(v, dmns, info);
            String value = "?";
            if (v.getBounds().getCases() != null && !v.getBounds().getCases().isEmpty()) {
                value = v.getBounds().getCases().get(0);
            } else if (v.getBounds().getMin() != null) {
                value = String.valueOf(v.getBounds().getMin() + (v.getBounds().isMinExclusive() ? 1 : 0));
            }
            result += v.getName() + "=" + value + "\n#" + v.getBounds() + "\n";
        }
        return result;
    }

}
