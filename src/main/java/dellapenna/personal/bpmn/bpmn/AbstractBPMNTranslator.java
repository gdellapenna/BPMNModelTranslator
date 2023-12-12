package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
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
 */
public abstract class AbstractBPMNTranslator<T> implements BPMNTranslator<T> {

    //i gateway joining chiamano translateflow
    //gli eventi (almneno start) chiamano translateflow
    private BPMNDecodedNamedFlow<T> translateFlow(FlowNode n) throws FeelTranslatorException {
        String flowid = "f_" + n.getId();
        T translated = translateFromNode(n);
        return new BPMNDecodedNamedFlow(flowid, translated);
    }

    private T translateFromNode(FlowNode n) throws FeelTranslatorException {
        List<T> result = new ArrayList<>();
        while (n != null) {
            BPMNDecodedStep<T> nodeflow = translateNode(n);
            result.add(nodeflow.code());
            n = nodeflow.nextStep();
        }
        return translateSequence(result);
    }

    private BPMNDecodedStep<T> translateNode(FlowNode n) throws FeelTranslatorException {
        BPMNDecodedStep<T> result;

        //HYP: i nodi hanno tutto un incoming e un outgoing TRANNE i gateway       
        if (n.getOutgoing().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
            //se un nodo ha più n.getOutgoing(), posporre un inclusive virtuale
            InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
            SequenceFlow virtualSequence = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
            virtualGateway.setName(n.getName() + " SPLIT GATEWAY");
            virtualGateway.setId(n.getId() + "XVG");
            n.getParentElement().addChildElement(virtualGateway);
            n.getParentElement().addChildElement(virtualSequence);
            virtualSequence.setSource(n);
            for (SequenceFlow seq : n.getOutgoing()) {
                seq.setSource(virtualGateway);
            }
            virtualGateway.getOutgoing().addAll(n.getOutgoing());
            virtualGateway.getIncoming().add(virtualSequence);
            virtualSequence.setTarget(virtualGateway);
            n.getOutgoing().clear();
            n.getOutgoing().add(virtualSequence);
        }
        if (n.getIncoming().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
            //se un nodo ha più ingoing, premettere un inclusive virtuale
            InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
            SequenceFlow virtualSequence = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
            virtualGateway.setName(n.getName() + " JOIN GATEWAY");
            virtualGateway.setId(n.getId() + "EVG");
            n.getParentElement().addChildElement(virtualGateway);
            n.getParentElement().addChildElement(virtualSequence);
            virtualSequence.setSource(virtualGateway);
            virtualGateway.getOutgoing().add(virtualSequence);
            for (SequenceFlow seq : n.getIncoming()) {
                seq.setTarget(virtualGateway);
            }
            virtualGateway.getIncoming().addAll(n.getIncoming());
            virtualSequence.setTarget(n);
            n.getIncoming().clear();
            n.getIncoming().add(virtualSequence);
            n = virtualGateway;
        }
        ///***DOVREMMO CREARE UN GATEWAY VIRTUALE IN OGNI CASO SE UN FLOW USCENTE HA UNA CONDIZIONE!****///
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.Event t -> {
                result = translateEventNode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = translateTaskNode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Gateway t -> {
                result = translateGatewayNode(t);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate expression node of type " + n.getClass().getName());
            }
        }
        return result;
    }

    private BPMNDecodedStep<T> translateGatewayNode(Gateway n) throws FeelTranslatorException {
        T result;

        boolean splitting = (n.getOutgoing().size() > 1);
        if (splitting) {
            List<BPMNDecodedConditionalFlow<T>> conditionalFlows = new ArrayList<>();
            for (SequenceFlow o : n.getOutgoing()) {
                T subFlow = translateFromNode(o.getTarget());
                conditionalFlows.add(new BPMNDecodedConditionalFlow<>(o.getConditionExpression().getTextContent(), subFlow));
            }

            switch (n) {
                case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                    result = translateExclusiveGateway(conditionalFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                    result = translateInclusiveGateway(conditionalFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                    result = translateEventGateway(conditionalFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                    result = translateParallelGateway(conditionalFlows);
                }
                default -> {
                    throw new FeelTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
                }
            }
        } else {
            //joining
            BPMNDecodedNamedFlow<T> joinedflow = translateFlow(n.getOutgoing().iterator().next().getTarget()); //HYP: ce n'è solo uno
            switch (n) {
                case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                    result = translateExclusiveJoiningGateway(joinedflow);
                }
                case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                    result = translateInclusiveJoiningGateway(joinedflow);
                }
                case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                    result = translateEventJoiningGateway(joinedflow);
                }
                case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                    result = translateParallelJoiningGateway(joinedflow);
                }
                default -> {
                    throw new FeelTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
                }
            }
        }
        return new BPMNDecodedStep<>(result, null);
    }

    private BPMNDecodedStep<T> translateEventNode(Event n) throws FeelTranslatorException {
        T result;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                result = translateStartEvent(t);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                result = translateEndEvent(t);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return new BPMNDecodedStep<>(result, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private BPMNDecodedStep<T> translateTaskNode(Task n) throws FeelTranslatorException {
        T result;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                result = translateBusinessRuleTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                result = translateReceiveTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                result = translateSendTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                result = translateServiceTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                result = translateUserTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                result = translateScriptTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                result = translateManualTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = translateGenericTask(t);
            }
        }
        //HYP: only one exiting!
        return new BPMNDecodedStep<>(result, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget());
    }

    @Override
    public T translate(BpmnModelInstance dmn) throws FeelTranslatorException {
        reset();
        Collection<StartEvent> start = dmn.getModelElementsByType(StartEvent.class);
        //ci possono essere più start su nodi comuni???
        for (StartEvent s : start) {
            BPMNDecodedNamedFlow<T> flow = translateFlow(s);
            translateNamedFlow(flow.name(),flow.code());
        }
        return finalizeTranslation();
    }

    protected abstract void reset();

    protected abstract T finalizeTranslation();

    protected abstract T translateNamedFlow(String name, T code);

    protected abstract T translateSequence(List<T> statements);

    protected abstract T translateManualTask(ManualTask t);

    protected abstract T translateScriptTask(ScriptTask t);

    protected abstract T translateUserTask(UserTask t);

    protected abstract T translateServiceTask(ServiceTask t);

    protected abstract T translateSendTask(SendTask t);

    protected abstract T translateReceiveTask(ReceiveTask t);

    protected abstract T translateBusinessRuleTask(BusinessRuleTask t);

    protected abstract T translateGenericTask(Task t);

    protected abstract T translateEndEvent(EndEvent t);

    protected abstract T translateStartEvent(StartEvent t);

    protected abstract T translateParallelJoiningGateway(BPMNDecodedNamedFlow<T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateEventJoiningGateway(BPMNDecodedNamedFlow<T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateInclusiveJoiningGateway(BPMNDecodedNamedFlow<T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateExclusiveJoiningGateway(BPMNDecodedNamedFlow<T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateParallelGateway(List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateEventGateway(List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateInclusiveGateway(List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateExclusiveGateway(List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException;

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
