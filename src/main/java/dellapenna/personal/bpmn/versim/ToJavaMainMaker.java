package dellapenna.personal.bpmn.versim;

import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import dellapenna.personal.bpmn.bpmn.VariableDefinition;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import java.util.Map;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public class ToJavaMainMaker {

//    public String generateProcessLauncher(BPMNDecodedModel bpmn, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
//        String mainMethod = "public static void main(String[] args) {\n";
//        if (info == null || !info.isDebug()) {
//            mainMethod += "BPMNExecProcessUtils.debugChannel=new java.io.PrintStream(java.io.OutputStream.nullOutputStream());";
//        }
//        if (info == null || info.isTrueParallel()) {
//            mainMethod += "BPMNExecProcessUtils.enableTrueParallel();";
//        }
//
//        //test for first process only
//        BPMNDecodedProcess process = bpmn.processes().get(0);
//        mainMethod += /*"bpmn_process_" +*/ sanitizeName(process.getName()) + " process = new " /*+ "bpmn_process_"*/ + sanitizeName(process.getName()) + "();\n";
//        mainMethod += "process.execute("
//                + process.getFreeVariables().stream().map(v -> "null" + "/*" + v.getName() + "*/").collect(Collectors.joining(","))
//                + ");";
//
//        mainMethod += "}";
//
//        return " class Executor" + " { " + mainMethod + "}";
//    }

//    public String generateInputCostraintNotes(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
//        String result = """
//                        
//               /*
//                * ****************************** Input Variable Constraints *************************
//                */
//               """;
//        VariableUtils vu = new VariableUtils();
//        for (VariableDefinition v : process.getFreeVariables(info)) {
//            vu.analyzeInputConstraints(v, dmns, info);
//            result += "//" + v.getName() + ":" + v.getBounds() + "\n";
//        }
//        return result;
//    }

    /*
    //MIGRATE IN BPDMNTranslator
    public String generateInputProperties(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        String result = "";
        VariableUtils vu = new VariableUtils();
        for (VariableDefinition v : process.getFreeVariables(info)) {
            vu.analyzeInputConstraints(v, dmns, info);
            String value = "?";
            if (v.getBounds().getCases() != null && !v.getBounds().getCases().isEmpty()) {
                value = v.getBounds().getCases().toArray(new String[0])[0];
            } else if (v.getBounds().getMin() != null) {
                value = String.valueOf(v.getBounds().getMin() + (v.getBounds().isMinExclusive() ? 1 : 0));
            }
            result += v.getName() + "=" + value + "\n#" + v.getBounds() + "\n";
        }
        return result;
    }

    public String generateTraceProperties(BPMNDecodedProcess process, DMNDecodedModel<String>[] dmns, BPMNTranslationInfo info) {
        String result = "# nodes\n";
        for (Map.Entry<FlowNode, BPMNDecodedProcess.FlowNodeInfo> e : process.getGraph().entrySet()) {
            FlowNode s = e.getKey();
            result += s.getId() + "[" + (s.getName() != null ? ("label=\"" + s.getName() + "\"") : "") + "]" + "\n";
        }

        result += "\n\n# edges\n";
        for (Map.Entry<FlowNode, BPMNDecodedProcess.FlowNodeInfo> e : process.getGraph().entrySet()) {
            FlowNode s = e.getKey();
            for (FlowNode t : e.getValue().getOutgoingEdges()) {
                result += "\"" + s.getId() + "\"" + " -> " + "\"" + t.getId() + "\"" + "\n";
            }
        }

        return result;
    }
*/
}
