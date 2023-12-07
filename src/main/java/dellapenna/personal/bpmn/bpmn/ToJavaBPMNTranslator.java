package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import dellapenna.personal.bpmn.feel.ToJavaFeelTranslator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.camunda.bpm.model.xml.type.ModelElementType;

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private final static String ZEEBENS = "http://camunda.org/schema/zeebe/1.0";

    private final Map<String, BPMNNamedFlow<String>> codeBlocks = new HashMap<>();
    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    @Override
    protected void reset() {
        codeBlocks.clear();
    }

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
    }

    @Override
    protected String translateNamedFlow(BPMNNamedFlow<String> flow) {
        return "public void " + flow.name() + "() {\n"
                + flow.code()
                + "\n}";
    }

    @Override
    protected String translateFlowCollection(List<String> flows) {
        return Stream.concat(
                flows.stream(),
                codeBlocks.values().stream().map(fd -> translateNamedFlow(fd)))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    protected String translateGenericTask(Task t) {
        return sanitizeName("t_g_" + t.getName()) + "()";
    }

    @Override
    protected String translateManualTask(ManualTask t) {
        return sanitizeName("t_m_" + t.getName()) + "()";
    }

    @Override
    protected String translateScriptTask(ScriptTask t) {
        return sanitizeName("t_s_" + t.getName()) + "()";
    }

    @Override
    protected String translateUserTask(UserTask t) {
        return sanitizeName("t_u_" + t.getName()) + "()";
    }

    @Override
    protected String translateServiceTask(ServiceTask t) {
        return sanitizeName("t_a_" + t.getName()) + "()";
    }

    @Override
    protected String translateSendTask(SendTask t) {
        return sanitizeName("t_send_" + t.getName()) + "()";
    }

    @Override
    protected String translateReceiveTask(ReceiveTask t) {
        return sanitizeName("t_receive_" + t.getName()) + "()";
    }

    @Override
    protected String translateBusinessRuleTask(BusinessRuleTask t) {
        ModelElementInstance ioMapping = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "ioMapping");
        ModelElementInstance calledDecision = t.getExtensionElements().getUniqueChildElementByNameNs(ZEEBENS, "calledDecision");
        return (t.getName() != null ? "//" + t.getName() + "\n" : "")
                + "var " + calledDecision.getAttributeValue("resultVariable")
                + "=" + sanitizeName("dmn_" + calledDecision.getAttributeValue("decisionId"))
                + "("
                + "{" + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "input").stream()
                        .map(e -> e.getAttribute("target") + ":" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(",")) + "}"
                + ");\n"
                + ioMapping.getDomElement().getChildElementsByNameNs(ZEEBENS, "output").stream()
                        .map(e -> "var " + e.getAttribute("target") + "=" + feel.translateChecked(e.getAttribute("source").substring(1))).collect(Collectors.joining(";\n"));
    }

    @Override
    protected String translateEndEvent(EndEvent t) {
        return "//" + t.getName() + "\n" + "System.exit(0)";
    }

    @Override
    protected String translateStartEvent(StartEvent t) {
        return "//start: " + t.getName(); //inserire una condizione??
    }

    @Override
    protected String translateSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n"));
    }

    @Override
    protected String translateParallelJoiningGateway(BPMNNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventJoiningGateway(BPMNNamedFlow<String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveJoiningGateway(BPMNNamedFlow<String> joinedflow) throws FeelTranslatorException {
        codeBlocks.put(joinedflow.name(), joinedflow);
        return joinedflow.name() + "()";
    }

    @Override
    protected String translateExclusiveJoiningGateway(BPMNNamedFlow<String> joinedflow) throws FeelTranslatorException {
        codeBlocks.put(joinedflow.name(), joinedflow);
        return joinedflow.name() + "()";
    }

    @Override
    protected String translateParallelGateway(List<BPMNConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventGateway(List<BPMNConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveGateway(List<BPMNConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
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
    protected String translateExclusiveGateway(List<BPMNConditionalFlow<String>> splitFlows) throws FeelTranslatorException {
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
        return result;
    }

}
