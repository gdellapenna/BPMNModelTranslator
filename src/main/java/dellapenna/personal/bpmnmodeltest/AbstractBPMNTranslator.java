package dellapenna.personal.bpmnmodeltest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;

/**
 *
 * @author giuse
 * @param <T>
 * //VANNO MEMORIZZATI GLI ID DEI FLUSSI IN MODO DA CREARE GLI AGGANCI!!!!!!!!!!!!
 */
public class AbstractBPMNTranslator<T> implements BPMNTranslator<T> {
    
    Map<String,T> translated_nodes = new HashMap<>();

    private T translateNode(ConditionExpression condition, FlowNode n) throws FeelTranslatorException {
        T result;

        List<T> outgoing_flows = n.getOutgoing().stream().map(s -> translateNode(s.getConditionExpression(), s.getTarget())).toList();
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.Event t -> {
                result = translateEventNode(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = translateTaskNode(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.Gateway t -> {
                result = translateGatewayNode(t, outgoing_flows);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate expression node of type " + n.getClass().getName());
            }
        }
        translated_nodes.put(n.getId(), result); ////!!!!!
        return result;
    }

    private T translateConditionalFlow(ConditionExpression condition, List<T> outgoing_flows) throws FeelTranslatorException {

    }

    private T translateGatewayNode(Gateway n, List<T> outgoing_flows) throws FeelTranslatorException {
        T result;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                result = translateExclusiveGateway(t.getName(), outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                result = translateInclusiveGateway(t.getName(), outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                result = translateEventGateway(t.getName(), outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                result = translateParallelGateway(t.getName(), outgoing_flows);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }

        }
        return result;
    }

    private T translateEventNode(Event n, List<T> outgoing_flows) throws FeelTranslatorException {
        T result;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                result = translateStartEvent(t.getName(), outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                result = translateEndEvent(t.getName());
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return result;
    }

    private T translateTaskNode(Task n, List<T> outgoing_flows) throws FeelTranslatorException {
        T result;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                result = translateBusinessRuleTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                result = translateReceiveTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                result = translateSendTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                result = translateServiceTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                result = translateUserTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                result = translateScriptTask(t, outgoing_flows);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                result = translateManualTask(t, outgoing_flows);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate task node of type " + n.getClass().getName());
            }

        }
        return result;
    }

    @Override
    public Map<String, T> translate(BpmnModelInstance dmn) throws FeelTranslatorException {
        Collection<StartEvent> start = dmn.getModelElementsByType(StartEvent.class);
        //ci possono essere più start su nodi comuni???
        Map<String, T> result = new HashMap<>();
        for (StartEvent s : start) {
            result.put(s.getId(), translate(s));
        }
        return result;
    }

    @Override
    public T translate(StartEvent s) throws FeelTranslatorException {
        return translateNode(null, s);
    }

    protected abstract T translateManualTask(ManualTask t, List<T> outgoing_flows);

    protected abstract T translateScriptTask(ScriptTask t, List<T> outgoing_flows);

    protected abstract T translateUserTask(UserTask t, List<T> outgoing_flows);

    protected abstract T translateServiceTask(ServiceTask t, List<T> outgoing_flows);

    protected abstract T translateSendTask(SendTask t, List<T> outgoing_flows);

    protected abstract T translateReceiveTask(ReceiveTask t, List<T> outgoing_flows);

    protected abstract T translateBusinessRuleTask(BusinessRuleTask t, List<T> outgoing_flows);

    protected abstract T translateEndEvent(String name);

    protected abstract T translateStartEvent(String name, List<T> outgoing_flows);

    protected abstract T translateParallelGateway(String name, List<T> outgoing_flows);

    protected abstract T translateEventGateway(String name, List<T> outgoing_flows);

    protected abstract T translateInclusiveGateway(String name, List<T> outgoing_flows);

    protected abstract T translateExclusiveGateway(String name, List<T> outgoing_flows);

    //////////////
    public void dump(BpmnModelInstance dmn) {
        Collection<StartEvent> start = dmn.getModelElementsByType(StartEvent.class);
        //ci possono essere più start su nodi comuni???
        for (StartEvent s : start) {
            dump(s, 0);
        }
    }

    public void dump(FlowNode s, int indent) {
        System.out.println("[" + s.getElementType().getTypeName() + "] " + s.getName());
        if (s instanceof BusinessRuleTask dt) {
            System.out.println(dt);
        }

        Collection<SequenceFlow> outgoing = s.getOutgoing();
        for (SequenceFlow sf : outgoing) {
            //ConditionExpression co = s.getModelInstance().newInstance(ConditionExpression.class);
            //co.setTextContent("condizione");
            //sf.setConditionExpression(co);

            System.out.print(" ".repeat(indent + 1) + "-> ");
            System.out.print("<" + (sf.getName() != null ? sf.getName() : "") + (sf.getConditionExpression() != null ? sf.getConditionExpression().getTextContent() : "") + ">");
            System.out.print(" ");
            FlowNode t = sf.getTarget();

            dump(t, indent + 3);
        }
    }

}
