package dellapenna.personal.bpmn.bpmn;

/*
REQ: parallel join waits for all the previously started parallal branches
HYP: no nested parallels
DED: if no nested parallels are possible, then the joining gateway must be reached exactly as many times as the 
number of parallel branches before it starts the joined flow, even if it has other inner flows derifing from other
non parallel flow splits

1) the joined flow must have a guard like wait(parallel_name, number_of_brances_started) (these params must becarried on with the decoding until the join is reached)
2) the branches leading to the parallel join gw must signal(parallel_name)
3) when the liear flow decoding reached a parallel join gw, in some way we must preepend the wait to the joined flow (getOutgoing?)  code

 */
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
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
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
 * @param
 */
public abstract class AbstractBPMNTranslator {

    Set<FlowNode> generated_flows = new HashSet<>();
    Deque<FlowNode> flows_to_translate = new ArrayDeque<>();

    ///
    //protected abstract T generateProcessSource(String name, List flows_code);
    //protected abstract Code registerFlow(BPMNDecodedFlow flow);
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
    public BPMNDecoded decodeBpmn(BpmnModelInstance bpmn, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        List<BPMNDecodedProcess> process_definitions = new ArrayList<>();
        Collection<Process> processes = bpmn.getModelElementsByType(Process.class);
        for (Process process : processes) {
            reset();
            process_definitions.add(decodeProcessNode(process, opt));
        }
        return new BPMNDecoded(process_definitions);
    }

    ////////////////////////
    // Decode a BPMN process node to BPMNDecodedProcess structure (code + other info)
    ////////////////////////
    public BPMNDecodedProcess decodeProcessNode(Process p, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        //ci possono essere più start su nodi comuni???
        BPMNDecodedProcess dp = new BPMNDecodedProcess(p.getName() != null ? p.getName() : p.getId());
        flows_to_translate.addAll(p.getChildElementsByType(StartEvent.class));
        while (!flows_to_translate.isEmpty()) {
            FlowNode s = flows_to_translate.poll();
            decodeLinearFlow(dp, s, opt);
        }
        return dp;
    }

    ////////////////////////
    // Decode node sequences starting from the given gateway into BPMNDecodedConditionalFlows (trigger + info + start node)
    ////////////////////////
    private List<BPMNDecodedConditionalFlow> decodeOutgoingGatewayFlows(BPMNDecodedProcess p, Gateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        String defaultFlow = n.getAttributeValue("default");
        //decode outgoing flows
        List<BPMNDecodedConditionalFlow> conditional_subflows = new ArrayList<>();
        for (SequenceFlow o : n.getOutgoing()) {
            //BPMNDecodedFlow subflow = decodeLinearFlow(o.getTarget());
            flows_to_translate.addFirst(o.getTarget()); //schedule flow for decoding
            if (o.getConditionExpression() != null) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(getFlowName(o.getTarget()), o.getConditionExpression().getTextContent(), o.getTarget()));
            } else if (o.getId().equals(defaultFlow)) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(getFlowName(o.getTarget()), null, o.getTarget()));
            } else {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(getFlowName(o.getTarget()), "=true", o.getTarget())); //always enabled
            }
        }
        return conditional_subflows;
    }

    ////////////////////////
    // Decode a node sequence to BPMNDecodedFlow (code + info)
    ////////////////////////
    private void decodeLinearFlow(BPMNDecodedProcess p, FlowNode start, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        if (!generated_flows.contains(start)) {
            Code code = new Code();
            FlowNode current = start;
            while (current != null) {
                if (!generated_flows.contains(current)) {
                    BPMNDecodedNode decoded_node = decodeNode(p, current, opt);
                    code.append(decoded_node.code());
                    current = decoded_node.nextStep();
                } else {
                    //from this point, the flow has been already generated - just join with a call                
                    code.append(generateFlowJointCode(p, current, opt));
                    current = null;
                }
            }
            generated_flows.add(start);
            p.registerFlow(getFlowName(start), code);
        }
    }

    ////////////////////////
    // Decode other BPMN nodes to BPMNDecodedNode structures (code + other info)
    ////////////////////////
    private BPMNDecodedNode decodeNode(BPMNDecodedProcess p, FlowNode n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        BPMNDecodedNode result;

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
                result = decodeEventNode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = decodeTaskNode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.Gateway t -> {
                result = decodeGatewayNode(p, t, opt);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate expression node of type " + n.getClass().getName());
            }
        }
        return result;
    }

    private BPMNDecodedNode decodeSplittingGatewayNode(BPMNDecodedProcess p, Gateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {

        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode(generateExclusiveGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode(generateInclusiveGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode(generateEventGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedNode(generateParallelGatewayCode(p, t, opt), null); //nel caso parallel bisogna prevedere un nextStep che chiami la funzione di join (generata dal joining gateway?)
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode decodeJoiningGatewayNode(BPMNDecodedProcess p, Gateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode(generateExclusiveJoiningGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode(generateInclusiveJoiningGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode(generateEventJoiningGatewayCode(p, t, opt), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedNode(generateParallelJoiningGatewayCode(p, t, opt), null); //nel caso parallel si genera un loop di attesa sugli entranti...
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode decodeGatewayNode(BPMNDecodedProcess p, Gateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        boolean splitting = (n.getOutgoing().size() > 1);
        if (splitting) {
            return decodeSplittingGatewayNode(p, n, opt);
        } else {
            return decodeJoiningGatewayNode(p, n, opt);
        }
    }

    private BPMNDecodedNode decodeEventNode(BPMNDecodedProcess p, Event n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        Code code;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                code = generateStartEventCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                code = generateEndEventCode(p, t, opt);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return new BPMNDecodedNode(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private BPMNDecodedNode decodeTaskNode(BPMNDecodedProcess p, Task n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        Code code;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                code = generateBusinessRuleTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                code = generateReceiveTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                code = generateSendTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                code = generateServiceTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                code = generateUserTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                code = generateScriptTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                code = generateManualTaskCode(p, t, opt);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                code = generateGenericTaskCode(p, t, opt);
            }
        }

        return new BPMNDecodedNode(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only one exiting!
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

    public abstract String generateBpmnSource(BPMNDecoded bpmn, Options opt);

    //////////////////
    // Generate code for specific BPMN nodes
    //////////////////
    protected abstract Code generateFlowJointCode(BPMNDecodedProcess p, FlowNode start, Options opt);

    protected abstract Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, FlowNode joinedflow, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, FlowNode joinedflow, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, FlowNode joinedflow, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, FlowNode joinedflow, Options opt) throws FeelTranslatorException, BpmnTranslatorException;

    ////
    protected abstract Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateEndEventCode(BPMNDecodedProcess p, EndEvent t, Options opt) throws BpmnTranslatorException;

    protected abstract Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, Options opt) throws BpmnTranslatorException;

    ////
    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        return generateParallelGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, opt), opt);
    }

    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        return generateEventGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, opt), opt);
    }

    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        return generateInclusiveGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, opt), opt);
    }

    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        return generateExclusiveGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, opt), opt);
    }

    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, opt).get(0).firstStep();//HYP: ce n'è solo uno
        //enumerare gli step entranti
        n.getIncoming().stream().map(m -> getFlowName(m.getSource()) + "_trigger").toList();
        return generateParallelJoiningGatewayCode(p, n, outgoingFlow, opt);
    }

    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, opt).get(0).firstStep();//HYP: ce n'è solo uno
        return generateEventJoiningGatewayCode(p, n, outgoingFlow, opt);
    }

    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, opt).get(0).firstStep();//HYP: ce n'è solo uno
        return generateInclusiveJoiningGatewayCode(p, n, outgoingFlow, opt);

    }

    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, Options opt) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, opt).get(0).firstStep();//HYP: ce n'è solo uno
        return generateExclusiveJoiningGatewayCode(p, n, outgoingFlow, opt);
    }

    ////////////////////
    //i gateway joining chiamano registerFlow
    //gli eventi (almneno start) chiamano registerFlow
//    private BPMNDecodedFlow decodeFlowWithName(FlowNode n) throws FeelTranslatorException, BpmnTranslatorException {
//        //return new BPMNDecodedNamedFlow("flow_" + n.getId(), registerFlow(n));
//        return registerFlow("flow_" + n.getId(), n);
//    }
    //translates a linear (until translateNode returns a nextStep, i.e., without gateways) flow
//    private BPMNDecodedFlow decodeFlow(FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        return registerFlow(null, start);
//        
//
//    }
//    private BPMNDecodedFlow registerFlow(String name, FlowNode start) throws FeelTranslatorException, BpmnTranslatorException {
//        List code_sequence = new ArrayList<>();
//        FlowNode current = start, last = start;
//        while (current != null) {
//            BPMNDecodedNode nodeflow = decodeNode(current);
//            code_sequence.add(nodeflow.code());
//            last = current;
//            current = nodeflow.nextStep();
//        }
//        return new BPMNDecodedFlow(name, generateCompoundStatementCode(code_sequence), start, last);
//        //return generateCompoundStatementCode(code_sequence);
//    }
}
