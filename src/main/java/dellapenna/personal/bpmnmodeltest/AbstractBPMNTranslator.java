package dellapenna.personal.bpmnmodeltest;

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
 * @param <T> //VANNO MEMORIZZATI GLI ID DEI FLUSSI IN MODO DA CREARE GLI
 * AGGANCI!!!!!!!!!!!!
 */
public abstract class AbstractBPMNTranslator<T> implements BPMNTranslator<T> {

    //i gateway joining chiamano translateflow
    //gli eventi (almneno start) chiamano translateflow
    private Pair<String, T> translateFlow(FlowNode n) throws FeelTranslatorException {
        //List<Pair<String, T>> decodedFlows = new ArrayList<>();
        String flowid = "f_" + n.getId();
        T translated = translateFrom(n);
        return new Pair<>(flowid, translated);
    }

    private T translateFrom(FlowNode n) throws FeelTranslatorException {
        List<T> result = new ArrayList<>();
        while (n != null) {
            Pair<T, FlowNode> nodeflow = translateNode(n);
            result.add(nodeflow.first());
            n = nodeflow.second();
        }
        return translateSequence(result);
    }

    private Pair<T, FlowNode> translateNode(FlowNode n) throws FeelTranslatorException {
        Pair<T, FlowNode> result;

        if (n.getOutgoing().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
            dump(n, 0);

            //se un nodo ha più n.getOutgoing(), posporre un inclusive virtuale
            InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
            SequenceFlow virtualSequence = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
            n.getParentElement().addChildElement(virtualGateway);
            n.getParentElement().addChildElement(virtualSequence);
            virtualGateway.setName(n.getName() + " SPLIT GATEWAY");
            virtualGateway.getOutgoing().addAll(n.getOutgoing());
            virtualSequence.setTarget(virtualGateway);
            virtualGateway.getIncoming().add(virtualSequence);
            virtualGateway.setId(n.getId() + "XVG");
            virtualSequence.setSource(n);
            n.getOutgoing().clear();
            n.getOutgoing().add(virtualSequence);

            dump(n, 0);
        }

        if (n.getIncoming().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {

            dump(n, 0);

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

            dump(n, 0);
        }

        //***DOVREMMO CREARE UN GATEWAY VIRTUALE IN OGNI CASO SE UN FLOW USCENTE HA UNA CONDIZIONE!
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.Event t -> {
                result = translateEventNode(t); //java=funzione booleana (throwing) o chiamata standard (catching)
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = translateTaskNode(t); //java=chiamata a funzione
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

    private Pair<T, FlowNode> translateGatewayNode(Gateway n) throws FeelTranslatorException {
        T result;

        boolean splitting = (n.getOutgoing().size() > 1);
        //dobbiamo sfruttare i join gateway per riunire i flussi se appartenenti allo stesso livello (possiamo vedere l'id?) oppure 
        //riunirli trasformando il flusso comune in una chiamata a procedura... oppure lasciarli duplicati :)
        //via semplice: ogni gateway crea dei sotto-metodi che chiamiamo...

        if (splitting) {
            List<Pair<String, T>> splitFlows = new ArrayList<>();
            for (SequenceFlow o : n.getOutgoing()) {
                T splitFlow = translateFrom(o.getTarget());
                splitFlows.add(new Pair<>(o.getConditionExpression().getTextContent(), splitFlow));
            }

            switch (n) {
                case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                    result = translateExclusiveGateway(splitFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                    result = translateInclusiveGateway(splitFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                    result = translateEventGateway(splitFlows);
                }
                case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                    result = translateParallelGateway(splitFlows);
                }
                default -> {
                    throw new FeelTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
                }
            }
        } else {
            //joining
            Pair<String, T> joinedflow = translateFlow(n.getOutgoing().iterator().next().getTarget()); //HYP: ce n'è solo uno
            switch (n) {
                case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                    result = translateExclusiveJoiningGateway(joinedflow);
                    //Java: return a CALL to joinedflow.flowId, add joinedflow.flow to the list of procedures
                }
                case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                    result = translateInclusiveJoiningGateway(joinedflow);
                    //Java: CALL joinedflow.flowId, add joinedflow.flow to the list of procedures
                }
                case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                    result = translateEventJoiningGateway(joinedflow);
                    //Java: BOH
                }
                case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                    result = translateParallelJoiningGateway(joinedflow);
                    //Java: should make a JOIN
                }
                default -> {
                    throw new FeelTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
                }
            }
        }
        return new Pair<>(result, null);
    }

    private Pair<T, FlowNode> translateEventNode(Event n) throws FeelTranslatorException {
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
        return new Pair<>(result, n.getOutgoing().isEmpty()?null:n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private Pair<T, FlowNode> translateTaskNode(Task n) throws FeelTranslatorException {
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
        return new Pair<>(result, n.getOutgoing().isEmpty()?null:n.getOutgoing().iterator().next().getTarget()); //HYP: only one exiting!
    }

    @Override
    public T translate(BpmnModelInstance dmn) throws FeelTranslatorException {
        reset();
        Collection<StartEvent> start = dmn.getModelElementsByType(StartEvent.class);
        //ci possono essere più start su nodi comuni???
        List<T> result = new ArrayList<>();
        for (StartEvent s : start) {
            Pair<String, T> flow = translateFlow(s);
            result.add(translateNamedFlow(flow.first(), flow.second()));
        }
        return translateFlowCollection(result);
    }

    protected abstract void reset();

    protected abstract T translateFlowCollection(List<T> flows);

    protected abstract T translateNamedFlow(String flowid, T flow);

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

    protected abstract T translateParallelJoiningGateway(Pair<String, T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateEventJoiningGateway(Pair<String, T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateInclusiveJoiningGateway(Pair<String, T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateExclusiveJoiningGateway(Pair<String, T> joinedflow) throws FeelTranslatorException;

    protected abstract T translateParallelGateway(List<Pair<String, T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateEventGateway(List<Pair<String, T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateInclusiveGateway(List<Pair<String, T>> splitFlows) throws FeelTranslatorException;

    protected abstract T translateExclusiveGateway(List<Pair<String, T>> splitFlows) throws FeelTranslatorException;

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
