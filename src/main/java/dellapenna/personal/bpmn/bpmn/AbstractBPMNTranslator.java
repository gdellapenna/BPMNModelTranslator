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
    

    //protected abstract T generateProcessSource(String name, List<T> flows_code);
    //protected abstract Code generateFlowCode(BPMNDecodedFlow<T> flow);
    
    
    /////////////////////
    // Utilities
    /////////////////////
    protected void reset() {
        generated_flows.clear();
        flows_to_translate.clear();
    }

    protected String getFlowName(FlowNode start) {
        return "flow_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
    }

    ////////////////////////
    // Decode a BPMN instance to BPMNDecoded structure
    ////////////////////////
    @Override
    public BPMNDecoded<T> decodeBpmn(BpmnModelInstance bpmn) throws FeelTranslatorException, BpmnTranslatorException {
        List<BPMNDecodedProcess<T>> process_definitions = new ArrayList<>();
        Collection<Process> processes = bpmn.getModelElementsByType(Process.class);
        for (Process process : processes) {
            reset();
            process_definitions.add(decodeProcessNode(process));
        }
        return new BPMNDecoded<>(process_definitions);
    }

    ////////////////////////
    // Decode a BPMN process node to BPMNDecodedProcess structure (code + other info)
    ////////////////////////
    @Override
    public BPMNDecodedProcess<T> decodeProcessNode(Process p) throws FeelTranslatorException, BpmnTranslatorException {
        //ci possono essere più start su nodi comuni???        
        flows_to_translate.addAll(p.getChildElementsByType(StartEvent.class));
        List<BPMNDecodedFlow<T>> flows = new ArrayList<>();
        while (!flows_to_translate.isEmpty()) {
            FlowNode s = flows_to_translate.poll();
            BPMNDecodedFlow<T> flow = decodeLinearFlow(s); //null if already generated
            if (flow != null) {
                flows.add(flow);
            }
        }
        return new BPMNDecodedProcess<>(p.getName() != null ? p.getName() : p.getId(), flows);
    }

    ////////////////////////
    // Decode node sequences starting from the given gateway into BPMNDecodedConditionalFlows (trigger + info + start node)
    ////////////////////////
    private List<BPMNDecodedConditionalFlow<T>> decodeOutgoingGatewayFlows(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
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

    ////////////////////////
    // Decode a node sequence to BPMNDecodedFlow (code + info)
    ////////////////////////
    private BPMNDecodedFlow<T> decodeLinearFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
        if (!generated_flows.contains(start)) {
            Code<T> code = new Code();
            FlowNode current = start;
            while (current != null) {
                if (!generated_flows.contains(current)) {
                    BPMNDecodedNode<T> decoded_node = decodeNode(current);
                    code.append(decoded_node.code());
                    current = decoded_node.nextStep();
                } else {
                    //from this point, the flow has been already generated - just join with a call                
                    code.append(generateNodeJointCode(current));
                    current = null;
                }
            }
            generated_flows.add(start);
            return new BPMNDecodedFlow<>(getFlowName(start), code, start);
        } else {
            return null;
        }
    }

    ////////////////////////
    // Decode other BPMN nodes to BPMNDecodedNode structures (code + other info)
    ////////////////////////
    private BPMNDecodedNode<T> decodeNode(FlowNode n) throws FeelTranslatorException, BpmnTranslatorException {
        BPMNDecodedNode<T> result;

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

    private BPMNDecodedNode<T> decodeSplittingGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {

        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode<>(generateExclusiveGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode<>(generateInclusiveGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode<>(generateEventGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedNode<>(generateParallelGatewayCode(t), null); //nel caso parallel bisogna prevedere un nextStep che chiami la funzione di join (generata dal joining gateway?)
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode<T> decodeJoiningGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode<>(generateExclusiveJoiningGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode<>(generateInclusiveJoiningGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode<>(generateEventJoiningGatewayCode(t), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedNode<>(generateParallelJoiningGatewayCode(t), null); //nel caso parallel si genera un loop di attesa sugli entranti...
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode<T> decodeGatewayNode(Gateway n) throws FeelTranslatorException, BpmnTranslatorException {
        boolean splitting = (n.getOutgoing().size() > 1);
        if (splitting) {
            return decodeSplittingGatewayNode(n);
        } else {
            return decodeJoiningGatewayNode(n);
        }
    }

    private BPMNDecodedNode<T> decodeEventNode(Event n) throws FeelTranslatorException, BpmnTranslatorException {
        Code<T> code;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                code = generateStartEventCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                code = generateEndEventCode(t);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return new BPMNDecodedNode<>(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private BPMNDecodedNode<T> decodeTaskNode(Task n) throws FeelTranslatorException, BpmnTranslatorException {
        Code<T> code;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                code = generateBusinessRuleTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                code = generateReceiveTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                code = generateSendTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                code = generateServiceTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                code = generateUserTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                code = generateScriptTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                code = generateManualTaskCode(t);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                code = generateGenericTaskCode(t);
            }
        }

        return new BPMNDecodedNode<>(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only one exiting!
    }

    ////////////////////
    // Diagnostic functions
    ////////////////////
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

    //////////////////
    // Generate code for specific BPMN nodes
    //////////////////
    protected abstract Code generateNodeJointCode(FlowNode start);

    protected abstract Code generateParallelGatewayCode(ParallelGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventGatewayCode(EventBasedGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveGatewayCode(InclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveGatewayCode(ExclusiveGateway n, List<BPMNDecodedConditionalFlow<T>> splitFlows) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateParallelJoiningGatewayCode(ParallelGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventJoiningGatewayCode(EventBasedGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveJoiningGatewayCode(InclusiveGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveJoiningGatewayCode(ExclusiveGateway n, FlowNode joinedflow) throws FeelTranslatorException, BpmnTranslatorException;

    @Override
    public Code<T> generateParallelGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return generateParallelGatewayCode(n, decodeOutgoingGatewayFlows(n));
    }

    @Override
    public Code<T> generateEventGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return generateEventGatewayCode(n, decodeOutgoingGatewayFlows(n));
    }

    @Override
    public Code<T> generateInclusiveGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return generateInclusiveGatewayCode(n, decodeOutgoingGatewayFlows(n));
    }

    @Override
    public Code<T> generateExclusiveGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        return generateExclusiveGatewayCode(n, decodeOutgoingGatewayFlows(n));
    }

    @Override
    public Code<T> generateParallelJoiningGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        //enumerare gli step entranti
        n.getIncoming().stream().map(m -> getFlowName(m.getSource()) + "_trigger").toList();
        return generateParallelJoiningGatewayCode(n, outgoingFlow);
    }

    @Override
    public Code<T> generateEventJoiningGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return generateEventJoiningGatewayCode(n, outgoingFlow);
    }

    @Override
    public Code<T> generateInclusiveJoiningGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return generateInclusiveJoiningGatewayCode(n, outgoingFlow);

    }

    @Override
    public Code<T> generateExclusiveJoiningGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(n).get(0).firstStep();//HYP: ce n'è solo uno
        return generateExclusiveJoiningGatewayCode(n, outgoingFlow);
    }

    ////////////////////
    //i gateway joining chiamano generateFlowCode
    //gli eventi (almneno start) chiamano generateFlowCode
//    private BPMNDecodedFlow<T> decodeFlowWithName(FlowNode n) throws FeelTranslatorException, BpmnTranslatorException {
//        //return new BPMNDecodedNamedFlow("flow_" + n.getId(), generateFlowCode(n));
//        return generateFlowCode("flow_" + n.getId(), n);
//    }
    //translates a linear (until translateNode returns a nextStep, i.e., without gateways) flow
//    private BPMNDecodedFlow<T> decodeFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        return generateFlowCode(null, start);
//        
//
//    }
//    private BPMNDecodedFlow<T> generateFlowCode(String name, FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        List<T> code_sequence = new ArrayList<>();
//        FlowNode current = start, last = start;
//        while (current != null) {
//            BPMNDecodedNode<T> nodeflow = decodeNode(current);
//            code_sequence.add(nodeflow.code());
//            last = current;
//            current = nodeflow.nextStep();
//        }
//        return new BPMNDecodedFlow<>(name, generateCompoundStatementCode(code_sequence), start, last);
//        //return generateCompoundStatementCode(code_sequence);
//    }
}
