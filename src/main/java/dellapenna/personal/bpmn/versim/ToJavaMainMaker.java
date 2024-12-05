package dellapenna.personal.bpmn.versim;

import dellapenna.personal.bpmn.bpmn.BPMNDecoded;
import dellapenna.personal.bpmn.bpmn.BPMNDecodedProcess;
import dellapenna.personal.bpmn.bpmn.BPMNTranslationInfo;
import static dellapenna.personal.bpmn.bpmn.ToJavaBPMNTranslator.sanitizeName;
import dellapenna.personal.bpmn.dmn.DMNDecodedModel;
import dellapenna.personal.bpmn.dmn.DMNDecodedTable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author giuse
 */
public class ToJavaMainMaker {

    //dovremmo memorizzare il nome del parametro target quando effetuiamo la chiamata alla DMN per capire che colonna estrarre
    //servirebbe l'espressione feel originale per poter capire se ci sono range
    public void findDMNConstraints(DMNDecodedModel<String>[] dmns, String table_id, String input_name) {
        DMNDecodedTable<String> table = Arrays.stream(dmns).flatMap(dmn -> dmn.tables().values().stream()).filter(t -> t.id().equals(table_id)).findFirst().orElse(null);
        if (table != null) {
            List<String> conditions = table.rules().stream().flatMap(r -> r.conditions().stream()).filter(c -> c.inputExpression().equals(input_name)).map(c -> c.testExpression()).toList();
            conditions.stream().forEach(c -> System.out.println(c)); //DEBUG
        }

    }

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

}
