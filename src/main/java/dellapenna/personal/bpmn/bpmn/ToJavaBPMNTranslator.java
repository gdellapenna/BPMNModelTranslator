package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";

    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    @Override
    protected void reset() {
        functions.clear();
    }

    private FunctionDefinition registerProcedure(String name) {
        return registerFunction(name, "\tSystem.out.println(\"" + name + "\");\n" + "\treturn null;");
    }

    private FunctionDefinition registerFunction(String name, String code) {
        FunctionDefinition f = new FunctionDefinition(sanitizeName(name), code, Void.class, Collections.EMPTY_MAP);
        functions.put(name, f);
        return f;
    }

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private String outputFunctionCode(FunctionDefinition f) {
        return "public " + f.returnType().getName() + " " + f.name() + "("
                + f.parameters().entrySet().stream().map(e -> e.getValue().getName() + " " + e.getKey()).collect(Collectors.joining(", "))
                + ") {\n"
                + f.body()
                + "\n}";
    }

    @Override
    protected String finalizeTranslation() {
        return functions.values().stream()
                .map(fd -> outputFunctionCode(fd))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    protected String translateNamedFlow(String name, String code) {
        return outputFunctionCode(registerFunction(name, code));
    }

    @Override
    protected String translateSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n"));
    }

    /////TASKS
    private String translateTask(String name) {
        FunctionDefinition proc = registerProcedure(name);
        return proc.name() + "()";
    }

    @Override
    protected String translateGenericTask(Task t) {
        return translateTask("t_g_" + t.getName());
    }

    @Override
    protected String translateManualTask(ManualTask t) {
        return translateTask("t_m_" + t.getName());
    }

    @Override
    protected String translateScriptTask(ScriptTask t) {
        return translateTask("t_s_" + t.getName());
    }

    @Override
    protected String translateUserTask(UserTask t) {
        return translateTask("t_u_" + t.getName());
    }

    @Override
    protected String translateServiceTask(ServiceTask t) {
        return translateTask("t_a_" + t.getName());
    }

    @Override
    protected String translateSendTask(SendTask t) {
        return translateTask("t_send_" + t.getName());
    }

    @Override
    protected String translateReceiveTask(ReceiveTask t) {
        return translateTask("t_recv_" + t.getName());
    }

    @Override
    protected String translateBusinessRuleTask(BusinessRuleTask t) {

        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");

        String procName = sanitizeName("dmn_" + calledDecision.getAttributeValue("decisionId"));
        String output_record_name = procName + "_result";

        return (t.getName() != null ? "//" + t.getName() : "")                                 
                + "\n\t" + output_record_name + " " + calledDecision.getAttributeValue("resultVariable")
                + "=" + procName
                + "("+ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> "/*" + e.getAttribute("target") + "*/" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(", "))
                +");\n"
                //da sostituire modificando nell'espressione ogni riferimento a var.campo in result.get("campo")
                + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                        .map(e -> "\tvar " + e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1)))
                        //.map(s -> s.replaceAll(calledDecision.getAttributeValue("resultVariable") + ".([a-z0-9_]+)", calledDecision.getAttributeValue("resultVariable") + ".get(\"$1\")"))
                        .collect(Collectors.joining(";\n"));
    }

    /////// EVENTS
    @Override
    protected String translateEndEvent(EndEvent t) {
        return "//end: " + t.getName() + "\n"
                + "System.exit(0);"
                + "return  null;";
    }

    @Override
    protected String translateStartEvent(StartEvent t) {
        return "//start: " + t.getName(); //inserire una condizione??
    }

    ////// GATEWAYS
    private String translateJoiningGateway(String name, String code) throws FeelTranslatorException {
        FunctionDefinition proc = registerFunction(name, code);
        return "return " + proc.name() + "();";
    }

    @Override
    protected String translateParallelJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        return translateJoiningGateway(joinedflow.name(), joinedflow.code());
    }

    @Override
    protected String translateExclusiveJoiningGateway(BPMNDecodedNamedFlow<String> joinedflow) throws FeelTranslatorException {
        return translateJoiningGateway(joinedflow.name(), joinedflow.code());
    }

    @Override
    protected String translateParallelGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            result += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" + "{\n" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + splitFlows.get(o).code()
                    + "\n}";
        }
        return result;
    }

    @Override
    protected String translateExclusiveGateway(List<BPMNDecodedConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            if (o > 0) {
                result += " else ";
            }
            result += "if "
                    + "(" + feel.translate(splitFlows.get(o).condition().substring(1)) + ")" + "{\n" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + splitFlows.get(o).code()
                    + "\n}";
        }
        result+=" else { return null; }";
        return result;
    }

}
