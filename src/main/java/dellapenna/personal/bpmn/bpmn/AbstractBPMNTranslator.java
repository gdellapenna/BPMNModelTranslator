package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;

/**
 *
 * @author giuse
 * @param <T>
 */
public abstract class AbstractBPMNTranslator<T> implements BPMNTranslator<T> {

    protected abstract void reset();

    protected abstract T translateBpmn(List<T> processes_code);

    protected abstract T translateProcess(String name, List<T> flows_code);

    protected abstract T translateParallelGateway(ParallelGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateEventGateway(EventBasedGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateInclusiveGateway(InclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateExclusiveGateway(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateParallelJoiningGateway(ParallelGateway n, BPMNDecodedFlow<T> joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateEventJoiningGateway(EventBasedGateway n, BPMNDecodedFlow<T> joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateInclusiveJoiningGateway(InclusiveGateway n, BPMNDecodedFlow<T> joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract T translateExclusiveJoiningGateway(ExclusiveGateway n, BPMNDecodedFlow<T> joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    ////
    protected abstract T translateCodeSequence(List<T> statements);

    protected abstract T translateFlow(BPMNDecodedFlow<T> flow);

    @Override
    public T translateBpmn(BpmnModelInstance bpmn) throws FeelTranslatorException, BpmnTranslatorException {
        List<T> processes_code = new ArrayList<>();
        Collection<Process> processes = bpmn.getModelElementsByType(Process.class);
        for (Process process : processes) {
            reset();
            processes_code.add(translateProcess(process));
        }
        return translateBpmn(processes_code); //internal call on decoded processes code
    }

    @Override
    public T translateProcess(Process p) throws FeelTranslatorException, BpmnTranslatorException {
        Collection<StartEvent> start = p.getChildElementsByType(StartEvent.class);
        //ci possono essere più start su nodi comuni???
        List<T> flows_code = new ArrayList<>();
        for (StartEvent s : start) {
            BPMNDecodedFlow<T> flow = decodeFlow(s);
            flows_code.add(translateFlow(flow));
        }
        return translateProcess(p.getName() != null ? p.getName() : p.getId(), flows_code); //internal call on decoded flows code
    }

    @Override
    public T translateParallelGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateParallelGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public T translateEventGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateEventGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public T translateInclusiveGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateInclusiveGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public T translateExclusiveGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateExclusiveGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public T translateParallelJoiningGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateParallelJoiningGateway(n,
                decodeFlow(n.getOutgoing().iterator().next().getTarget())//HYP: ce n'è solo uno
        );
    }

    @Override
    public T translateEventJoiningGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateEventJoiningGateway(n,
                decodeFlow(n.getOutgoing().iterator().next().getTarget())//HYP: ce n'è solo uno
        );
    }

    @Override
    public T translateInclusiveJoiningGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateInclusiveJoiningGateway(n,
                decodeFlow(n.getOutgoing().iterator().next().getTarget())//HYP: ce n'è solo uno
        );
    }

    @Override
    public T translateExclusiveJoiningGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateExclusiveJoiningGateway(n,
                decodeFlow(n.getOutgoing().iterator().next().getTarget())//HYP: ce n'è solo uno
        );
    }

    ////////////////////////
    private BPMNDecodedStep<T> decodeNode(FlowNode n) throws FeelTranslatorException, BpmnTranslatorException {
        BPMNDecodedStep<T> result;

        //HYP: i nodi hanno tutti un incoming e un outgoing TRANNE i gateway       
        if (n.getOutgoing().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
            //se un nodo ha più n.getOutgoing(), posporre un inclusive virtuale
            org.camunda.bpm.model.bpmn.instance.InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
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
            org.camunda.bpm.model.bpmn.instance.InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
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
                result = decodeEventNode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = decodeTaskNode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Gateway t -> {
                result = decodeGatewayNode(t);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate expression node of type " + n.getClass().getName());
            }
        }
        return result;
    }

    private BPMNDecodedStep<T> decodeSplittingGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {

        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedStep<>(translateExclusiveGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedStep<>(translateInclusiveGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedStep<>(translateEventGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedStep<>(translateParallelGateway(t), null); //nel caso parallel bisogna prevedere un nextStep che chiami la funzione di join (generata dal joining gateway?)
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedStep<T> decodeJoiningGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedStep<>(translateExclusiveJoiningGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedStep<>(translateInclusiveJoiningGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedStep<>(translateEventJoiningGateway(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedStep<>(translateParallelJoiningGateway(t), null); //nel caso parallel si genera un loop di attesa sugli entranti...
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedStep<T> decodeGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        boolean splitting = (n.getOutgoing().size() > 1);
        if (splitting) {
            return decodeSplittingGatewayNode(n);
        } else {
            return decodeJoiningGatewayNode(n);
        }
    }

    private BPMNDecodedStep<T> decodeEventNode(Event n) throws FeelTranslatorException, BpmnTranslatorException {
        T code;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                code = translateStartEvent(t);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                code = translateEndEvent(t);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return new BPMNDecodedStep<>(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private BPMNDecodedStep<T> decodeTaskNode(Task n) throws FeelTranslatorException, BpmnTranslatorException {
        T code;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                code = translateBusinessRuleTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                code = translateReceiveTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                code = translateSendTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                code = translateServiceTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                code = translateUserTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                code = translateScriptTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                code = translateManualTask(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                code = translateGenericTask(t);
            }
        }

        return new BPMNDecodedStep<>(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only one exiting!
    }

    ////////////////////
    //i gateway joining chiamano translateFlow
    //gli eventi (almneno start) chiamano translateFlow
//    private BPMNDecodedFlow<T> decodeFlowWithName(FlowNode n) throws FeelTranslatorException, BpmnTranslatorException {
//        //return new BPMNDecodedNamedFlow("flow_" + n.getId(), translateFlow(n));
//        return translateFlow("flow_" + n.getId(), n);
//    }
    //translates a linear (until translateNode returns a nextStep, i.e., without gateways) flow
//    private BPMNDecodedFlow<T> decodeFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        return translateFlow(null, start);
//        
//
//    }
//    private BPMNDecodedFlow<T> translateFlow(String name, FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        List<T> code_sequence = new ArrayList<>();
//        FlowNode current = start, last = start;
//        while (current != null) {
//            BPMNDecodedStep<T> nodeflow = decodeNode(current);
//            code_sequence.add(nodeflow.code());
//            last = current;
//            current = nodeflow.nextStep();
//        }
//        return new BPMNDecodedFlow<>(name, translateCodeSequence(code_sequence), start, last);
//        //return translateCodeSequence(code_sequence);
//    }
    
    private BPMNDecodedFlow<T> decodeFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
        List<T> code_sequence = new ArrayList<>();
        FlowNode current = start, last = start;
        while (current != null) {
            BPMNDecodedStep<T> nodeflow = decodeNode(current);
            code_sequence.add(nodeflow.code());
            last = current;
            current = nodeflow.nextStep();
        }
        return new BPMNDecodedFlow<>("flow_" + start.getId(), translateCodeSequence(code_sequence), start, last);
    }

    private List<BPMNDecodedConditionalFlow<T>> decodeOutgoingFlows(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        String defaultFlow = n.getAttributeValue("default");
        //decode outgoing flows
        List<BPMNDecodedConditionalFlow<T>> conditional_subflows = new ArrayList<>();
        for (SequenceFlow o : n.getOutgoing()) {
            BPMNDecodedFlow<T> subflow = decodeFlow(o.getTarget());
            if (o.getConditionExpression() != null) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(subflow.name(), o.getConditionExpression().getTextContent(), subflow.code(), subflow.firstStep(), subflow.lastStep()));
            } else if (o.getId().equals(defaultFlow)) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(subflow.name(), null, subflow.code(), subflow.firstStep(), subflow.lastStep()));
            } else {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(subflow.name(), "=true", subflow.code(), subflow.firstStep(), subflow.lastStep())); //always enabled
            }
        }
        return conditional_subflows;
    }

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
