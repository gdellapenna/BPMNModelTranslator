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
 * @param <T>
 * @param
 */
public abstract class AbstractBPMNTranslator<T> implements BPMNTranslator<T> {

    Set<FlowNode> generated_flows = new HashSet<>();
    Deque<FlowNode> flows_to_translate = new ArrayDeque<>();
    Set<ParallelGateway> parallel_joining = new HashSet<>();

    ///
    //protected abstract T generateProcessSource(String name, List flows_code);
    //protected abstract Code registerNodeProcedure(BPMNDecodedFlow flow);
    /////////////////////
    // Utilities
    /////////////////////
    protected void reset() {
        generated_flows.clear();
        flows_to_translate.clear();
    }

    @Override
    public T translate(BpmnModelInstance bpmn, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        return generateBpmnSource(decodeBpmn(bpmn, info), info);
    }

    ////////////////////////
    // Decode a BPMN instance to BPMNDecoded structure
    ////////////////////////
    public BPMNDecoded decodeBpmn(BpmnModelInstance bpmn, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        List<BPMNDecodedProcess> process_definitions = new ArrayList<>();
        Collection<Process> processes = bpmn.getModelElementsByType(Process.class);
        for (Process process : processes) {
            reset();
            process_definitions.add(decodeProcessNode(process, info));
        }
        return new BPMNDecoded(process_definitions);
    }

    ////////////////////////
    // Decode a BPMN process node to BPMNDecodedProcess structure (code + other info)
    ////////////////////////
    public BPMNDecodedProcess decodeProcessNode(Process p, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        //ci possono essere più start su nodi comuni???
        BPMNDecodedProcess dp = new BPMNDecodedProcess(p.getName() != null ? p.getName() : p.getId());
        flows_to_translate.addAll(p.getChildElementsByType(StartEvent.class));
        while (!flows_to_translate.isEmpty()) {
            FlowNode s = flows_to_translate.poll();
            decodeFlow(dp, s, info);
        }
        return dp;
    }

    ////////////////////////
    // Decode node sequences starting from the given gateway into BPMNDecodedConditionalFlows (trigger + info + start node)
    ////////////////////////
    private List<BPMNDecodedConditionalFlow> decodeOutgoingGatewayFlows(BPMNDecodedProcess p, Gateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        String defaultFlow = n.getAttributeValue("default");
        //decode outgoing flows
        List<BPMNDecodedConditionalFlow> conditional_subflows = new ArrayList<>();
        for (SequenceFlow o : n.getOutgoing()) {
            //BPMNDecodedFlow subflow = decodeLinearFlow(o.getTarget());
            flows_to_translate.addFirst(o.getTarget()); //schedule flow for decoding
            if (o.getConditionExpression() != null) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(p.getFlowName(o.getTarget()), o.getConditionExpression().getTextContent(), o.getTarget()));
            } else if (o.getId().equals(defaultFlow)) {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(p.getFlowName(o.getTarget()), null, o.getTarget()));
            } else {
                conditional_subflows.add(new BPMNDecodedConditionalFlow(p.getFlowName(o.getTarget()), "=true", o.getTarget())); //always enabled
            }
        }
        return conditional_subflows;
    }

    private void decodeFlow(BPMNDecodedProcess p, FlowNode start, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        //if (!generated_flows.contains(start)) {
        FlowNode current = start;
        while (current != null && !generated_flows.contains(current)) {
            if (current.getOutgoing().size() > 1 && !(current instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
                //se un nodo ha più n.getOutgoing(), posporre un inclusive virtuale
                org.camunda.bpm.model.bpmn.instance.InclusiveGateway virtualGateway = current.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
                SequenceFlow virtualSequence = current.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
                virtualGateway.setName(current.getName() + " SPLIT GATEWAY");
                virtualGateway.setId(current.getId() + "XVG");
                current.getParentElement().addChildElement(virtualGateway);
                current.getParentElement().addChildElement(virtualSequence);
                virtualSequence.setSource(current);
                for (SequenceFlow seq : current.getOutgoing()) {
                    seq.setSource(virtualGateway);
                }
                virtualGateway.getOutgoing().addAll(current.getOutgoing());
                virtualGateway.getIncoming().add(virtualSequence);
                virtualSequence.setTarget(virtualGateway);
                current.getOutgoing().clear();
                current.getOutgoing().add(virtualSequence);
            }
//            if (n.getIncoming().size() > 1 && !(n instanceof org.camunda.bpm.model.bpmn.instance.Gateway)) {
//                //se un nodo ha più ingoing, premettere un inclusive virtuale
//                org.camunda.bpm.model.bpmn.instance.InclusiveGateway virtualGateway = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.InclusiveGateway.class);
//                SequenceFlow virtualSequence = n.getModelInstance().newInstance(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
//                virtualGateway.setName(n.getName() + " JOIN GATEWAY");
//                virtualGateway.setId(n.getId() + "EVG");
//                n.getParentElement().addChildElement(virtualGateway);
//                n.getParentElement().addChildElement(virtualSequence);
//                virtualSequence.setSource(virtualGateway);
//                virtualGateway.getOutgoing().add(virtualSequence);
//                for (SequenceFlow seq : n.getIncoming()) {
//                    seq.setTarget(virtualGateway);
//                }
//                virtualGateway.getIncoming().addAll(n.getIncoming());
//                virtualSequence.setTarget(n);
//                n.getIncoming().clear();
//                n.getIncoming().add(virtualSequence);
//                n = virtualGateway;
//            }
            Code code = new Code<T>();
            BPMNDecodedNode decoded_node = decodeNode(p, current, info);
            code.append(decoded_node.code());
            generated_flows.add(current);
            p.registerNodeProcedure(current, code);
            //link to next step/node
            FlowNode next = decoded_node.nextStep();
            if (next != null) {
                code.append(generateFlowJointCode(p, current, next, info));
            }
            current = next;
        }

        //}
    }

    private BPMNDecodedNode decodeNode(BPMNDecodedProcess p, FlowNode n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        BPMNDecodedNode result;

        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.Event t -> {
                result = decodeEventNode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                result = decodeTaskNode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.Gateway t -> {
                result = decodeGatewayNode(p, t, info);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate expression node of type " + n.getClass().getName());
            }
        }
        return result;
    }

    private BPMNDecodedNode decodeSplittingGatewayNode(BPMNDecodedProcess p, Gateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        List<BPMNDecodedConditionalFlow> outgoing = decodeOutgoingGatewayFlows(p, n, info);
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode(generateExclusiveGatewayCode(p, t, outgoing, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode(generateInclusiveGatewayCode(p, t, outgoing, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode(generateEventGatewayCode(p, t, outgoing, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                return new BPMNDecodedNode(generateParallelGatewayCode(p, t, outgoing, info), null);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode decodeJoiningGatewayNode(BPMNDecodedProcess p, Gateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        FlowNode joiningStep = decodeOutgoingGatewayFlows(p, n, info).get(0).firstStep();

        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.ExclusiveGateway t -> {
                return new BPMNDecodedNode(generateExclusiveJoiningGatewayCode(p, t, joiningStep, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.InclusiveGateway t -> {
                return new BPMNDecodedNode(generateInclusiveJoiningGatewayCode(p, t, joiningStep, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.EventBasedGateway t -> {
                return new BPMNDecodedNode(generateEventJoiningGatewayCode(p, t, joiningStep, info), null);
            }
            case org.camunda.bpm.model.bpmn.instance.ParallelGateway t -> {
                parallel_joining.add(t);
                return new BPMNDecodedNode(generateParallelJoiningGatewayCode(p, t, joiningStep, info), null);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate gateway node of type " + n.getClass().getName());
            }
        }
    }

    private BPMNDecodedNode decodeGatewayNode(BPMNDecodedProcess p, Gateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        boolean splitting = (n.getOutgoing().size() > 1);
        if (splitting) {
            return decodeSplittingGatewayNode(p, n, info);
        } else {
            return decodeJoiningGatewayNode(p, n, info);
        }
    }

    private BPMNDecodedNode decodeEventNode(BPMNDecodedProcess p, Event n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        Code code;
        switch (n) {
            case org.camunda.bpm.model.bpmn.instance.StartEvent t -> {
                code = generateStartEventCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.EndEvent t -> {
                code = generateEndEventCode(p, t, info);
            }
            default -> {
                throw new BpmnTranslatorException("Cannot translate event node of type " + n.getClass().getName());
            }

        }
        return new BPMNDecodedNode(code, n.getOutgoing().isEmpty() ? null : n.getOutgoing().iterator().next().getTarget()); //HYP: only zero or one exiting!
    }

    private BPMNDecodedNode decodeTaskNode(BPMNDecodedProcess p, Task n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
        Code code;
        switch (n) {

            case org.camunda.bpm.model.bpmn.instance.BusinessRuleTask t -> {
                code = generateBusinessRuleTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.ReceiveTask t -> {
                code = generateReceiveTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.SendTask t -> {
                code = generateSendTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.ServiceTask t -> {
                code = generateServiceTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.UserTask t -> {
                code = generateUserTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.ScriptTask t -> {
                code = generateScriptTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.ManualTask t -> {
                code = generateManualTaskCode(p, t, info);
            }
            case org.camunda.bpm.model.bpmn.instance.Task t -> {
                code = generateGenericTaskCode(p, t, info);
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

    public abstract T generateBpmnSource(BPMNDecoded bpmn, BPMNTranslationInfo info);

    //////////////////
    // Generate code for specific BPMN nodes
    //////////////////
    protected abstract Code generateFlowJointCode(BPMNDecodedProcess p, FlowNode current, FlowNode next, BPMNTranslationInfo info);

    protected abstract Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, List<BPMNDecodedConditionalFlow> splitFlows, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, FlowNode joinedflow, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateManualTaskCode(BPMNDecodedProcess p, ManualTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateScriptTaskCode(BPMNDecodedProcess p, ScriptTask t, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException;

    protected abstract Code generateUserTaskCode(BPMNDecodedProcess p, UserTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateServiceTaskCode(BPMNDecodedProcess p, ServiceTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateSendTaskCode(BPMNDecodedProcess p, SendTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateReceiveTaskCode(BPMNDecodedProcess p, ReceiveTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateBusinessRuleTaskCode(BPMNDecodedProcess p, BusinessRuleTask t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateGenericTaskCode(BPMNDecodedProcess p, Task t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateEndEventCode(BPMNDecodedProcess p, EndEvent t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    protected abstract Code generateStartEventCode(BPMNDecodedProcess p, StartEvent t, BPMNTranslationInfo info) throws BpmnTranslatorException;

    ////
//    public Code generateParallelGatewayCode(BPMNDecodedProcess p, ParallelGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        return generateParallelGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, info), info);
//    }
//
//    public Code generateEventGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        return generateEventGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, info), info);
//    }
//
//    public Code generateInclusiveGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        return generateInclusiveGatewayCode(p, n,  decodeOutgoingGatewayFlows(p, n, info), info);
//    }
//
//    public Code generateExclusiveGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        return generateExclusiveGatewayCode(p, n, decodeOutgoingGatewayFlows(p, n, info), info);
//    }
//    public Code generateParallelJoiningGatewayCode(BPMNDecodedProcess p, ParallelGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, info).get(0).firstStep();//HYP: ce n'è solo uno
//        //enumerare gli step entranti
//        return generateParallelJoiningGatewayCode(p, n, outgoingFlow, info);
//    }
//
//    public Code generateEventJoiningGatewayCode(BPMNDecodedProcess p, EventBasedGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, info).get(0).firstStep();//HYP: ce n'è solo uno
//        return generateEventJoiningGatewayCode(p, n, outgoingFlow, info);
//    }
//
//    public Code generateInclusiveJoiningGatewayCode(BPMNDecodedProcess p, InclusiveGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, info).get(0).firstStep();//HYP: ce n'è solo uno
//        return generateInclusiveJoiningGatewayCode(p, n, outgoingFlow, info);
//
//    }
//
//    public Code generateExclusiveJoiningGatewayCode(BPMNDecodedProcess p, ExclusiveGateway n, BPMNTranslationInfo info) throws FeelTranslatorException, BpmnTranslatorException {
//        FlowNode outgoingFlow = decodeOutgoingGatewayFlows(p, n, info).get(0).firstStep();//HYP: ce n'è solo uno
//        return generateExclusiveJoiningGatewayCode(p, n, outgoingFlow, info);
//    }
}
