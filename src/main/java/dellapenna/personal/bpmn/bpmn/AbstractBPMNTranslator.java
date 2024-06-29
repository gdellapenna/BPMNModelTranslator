package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    Set<FlowNode> generated_flows = new HashSet<>();
    Deque<FlowNode> flows_to_translate = new ArrayDeque<>();

    ///
    protected abstract List<T> translateNodeJoint(FlowNode start);

    protected abstract T translateBpmn(List<T> processes_code);

    protected abstract T translateProcess(String name, List<T> flows_code);

    protected abstract List<T> translateParallelGateway(ParallelGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateEventGateway(EventBasedGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateInclusiveGateway(InclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateExclusiveGateway(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateParallelJoiningGateway(ParallelGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateEventJoiningGateway(EventBasedGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateInclusiveJoiningGateway(InclusiveGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract List<T> translateExclusiveJoiningGateway(ExclusiveGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    ////
   
    protected abstract List<T> translateFlow(BPMNDecodedFlow<T> flow);

    protected void reset() {
        generated_flows.clear();
        flows_to_translate.clear();
    }

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
        //ci possono essere più start su nodi comuni???        
        flows_to_translate.addAll(p.getChildElementsByType(StartEvent.class));
        List<T> flows_code = new ArrayList<>();
        while (!flows_to_translate.isEmpty()) {
            FlowNode s = flows_to_translate.poll();
            BPMNDecodedFlow<T> flow = decodeLinearFlow(s); //null if already generated
            if (flow != null) {
                flows_code.add(translateStatementSequence(translateFlow(flow)));
            }
        }
        return translateProcess(p.getName() != null ? p.getName() : p.getId(), flows_code); //internal call on decoded flows code
    }

    @Override
    public List<T> translateParallelGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateParallelGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public List<T> translateEventGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateEventGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public List<T> translateInclusiveGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateInclusiveGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public List<T> translateExclusiveGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return translateExclusiveGateway(n, decodeOutgoingFlows(n));
    }

    @Override
    public List<T> translateParallelJoiningGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        //enumerare gli step entranti
        n.getIncoming().stream().map(m->getFlowName(m.getSource())+"_trigger").toList();
        return translateParallelJoiningGateway(n, outgoingFlow);
    }

    @Override
    public List<T> translateEventJoiningGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return translateEventJoiningGateway(n, outgoingFlow);
    }

    @Override
    public List<T> translateInclusiveJoiningGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return translateInclusiveJoiningGateway(n, outgoingFlow);

    }

    @Override
    public List<T> translateExclusiveJoiningGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return translateExclusiveJoiningGateway(n, outgoingFlow);
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
        List<T> code;
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
        List<T> code;
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
//        return new BPMNDecodedFlow<>(name, translateStatementSequence(code_sequence), start, last);
//        //return translateStatementSequence(code_sequence);
//    }
    protected String getFlowName(FlowNode start) {
        return "flow_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
    }

    private BPMNDecodedFlow<T> decodeLinearFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {

        if (!generated_flows.contains(start)) {

            List<T> statements = new ArrayList<>();
            FlowNode current = start;

            while (current != null) {
                if (!generated_flows.contains(current)) {
                    BPMNDecodedStep<T> nodeflow = decodeNode(current);
                    statements.addAll(nodeflow.code());
                    current = nodeflow.nextStep();
                } else {
                    //from this point, the flow has been already generated - just join with a call                
                    statements.addAll(translateNodeJoint(current));
                    current = null;
                }
            }
            generated_flows.add(start);

            return new BPMNDecodedFlow<>(getFlowName(start), statements, start);
        } else {
            return null;
        }
    }

    private List<BPMNDecodedConditionalFlow<T>> decodeOutgoingFlows(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        String defaultFlow = n.getAttributeValue("default");
        //decode outgoing flows
        List<BPMNDecodedConditionalFlow<T>> conditional_subflows = new ArrayList<>();
        for (SequenceFlow o : n.getOutgoing()) {
            //BPMNDecodedFlow<T> subflow = decodeLinearFlow(o.getTarget());
            flows_to_translate.addFirst(o.getTarget()); //schedule flow for decoding
            if (o.getConditionExpression() != null) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(getFlowName(o.getTarget()), o.getConditionExpression().getTextContent(), o.getTarget()));
            } else if (o.getId().equals(defaultFlow)) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(getFlowName(o.getTarget()), null, o.getTarget()));
            } else {
                conditional_subflows.add(new BPMNDecodedConditionalFlow<>(getFlowName(o.getTarget()), "=true", o.getTarget())); //always enabled
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
