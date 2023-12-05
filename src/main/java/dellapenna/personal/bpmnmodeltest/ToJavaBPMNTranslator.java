package dellapenna.personal.bpmnmodeltest;

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

public class ToJavaBPMNTranslator extends AbstractBPMNTranslator<String> {

    private Map<String, FuctionDefinition> functions = new HashMap<>();
    private static final ToJavaFeelTranslator feel = new ToJavaFeelTranslator();

    private String sanitizeName(String n) {
        return n.replaceAll("[^A-Za-z0-9_]", "_");
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
        return t.getCamundaDecisionRefBinding() + "=" + sanitizeName("dmn_table_" + t.getCamundaDecisionRef());
    }

    @Override
    protected String translateEndEvent(EndEvent t) {
        return "System.exit(0)";
    }

    @Override
    protected String translateStartEvent(StartEvent t) {
        return "//start";
    }

    @Override
    protected String translateSequence(List<String> statements) {
        return statements.stream().collect(Collectors.joining(";\n"));
    }

    @Override
    protected String translateParallelJoiningGateway(Pair<String, String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventJoiningGateway(Pair<String, String> joinedflow) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveJoiningGateway(Pair<String, String> joinedflow) throws FeelTranslatorException {
        functions.put(joinedflow.first(), new FuctionDefinition(joinedflow.first(), joinedflow.second(), null, null));
        return joinedflow.first() + "()";
    }

    @Override
    protected String translateExclusiveJoiningGateway(Pair<String, String> joinedflow) throws FeelTranslatorException {
        functions.put(joinedflow.first(), new FuctionDefinition(joinedflow.first(), joinedflow.second(), null, null));
        return joinedflow.first() + "()";
    }

    @Override
    protected String translateParallelGateway(List<Pair<String, String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateEventGateway(List<Pair<String, String>> splitFlows) throws FeelTranslatorException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected String translateInclusiveGateway(List<Pair<String, String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            result += "if "
                    + "(" + feel.translate(splitFlows.get(o).first()) + ")" + "{"
                    + splitFlows.get(o).second()
                    + "\n}\n";
        }
        return result;
    }

    @Override
    protected String translateExclusiveGateway(List<Pair<String, String>> splitFlows) throws FeelTranslatorException {
        String result = "";
        for (int o = 0; o < splitFlows.size(); ++o) {
            if (o > 0) {
                result += " else ";
            }
            result += "if "
                    + "(" + feel.translate(splitFlows.get(o).first().substring(1)) + ")" + "{" //TEMP, dobbiamo togliere l'uguale se c'è altrimenti non è un'espressione feel
                    + splitFlows.get(o).second()
                    + "\n}\n";
        }
        return result;
    }

    @Override
    protected void reset() {
        functions.clear();
    }

    @Override
    protected String translateNamedFlow(String flowid, String flow) {
        return "public void " + flowid + "() {"
                + flow
                + "}\n\n";
    }

    @Override
    protected String translateFlowCollection(List<String> flows) {
        return Stream.concat(
                flows.stream(),
                functions.values().stream().map(fd -> translateNamedFlow(fd.name(), fd.body())))
                .collect(Collectors.joining("\n\n\n"));
    }

}
