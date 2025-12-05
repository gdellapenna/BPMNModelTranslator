package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Task;

/**
 *
 * @author Giuseppe Della Penna
 */
public class BPMNDecodedProcess {

    public enum NodeProcedureType {
        GATEWAY, EVENT, TASK, FLOW, GETTER, GENERAL
    };

    public enum VariableDirection {
        READ, WRITE, READWRITE
    };

    public static class FlowNodeInfo {

        FunctionDefinition generatedProcedure;
        List<FlowNode> outgoingEdges;

        public FlowNodeInfo(FunctionDefinition generatedProcedure) {
            this.generatedProcedure = generatedProcedure;
            this.outgoingEdges = new ArrayList<>();
        }

        public FunctionDefinition getGeneratedProcedure() {
            return generatedProcedure;
        }

        public void setGeneratedProcedure(FunctionDefinition generatedProcedure) {
            this.generatedProcedure = generatedProcedure;
        }

        public List<FlowNode> getOutgoingEdges() {
            return outgoingEdges;
        }
    }

    private final String name;
    private final List<BPMNDecodedFlow> rawFlows = new ArrayList<>();
    private final Map<NodeProcedureType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final List<VariableDefinition> processVariables = new ArrayList<>();
    private final List<MessageDefinition> processMessages = new ArrayList<>();
    private final Map<FlowNode, FlowNodeInfo> processGraphMap = new HashMap<>();

    private final List<String> startEventFlowNames = new ArrayList<>();

    public String getFlowName(FlowNode start) {
        NodeProcedureType type = switch (start) {
            case Gateway g ->
                NodeProcedureType.GATEWAY;
            case Task t ->
                NodeProcedureType.TASK;
            case Event e ->
                NodeProcedureType.EVENT;
            default ->
                NodeProcedureType.FLOW;
        };
        return type.toString() + "_" + start.getId() + (start.getName() != null && !start.getName().isBlank() ? "_" + start.getName() : "");
    }

    public BPMNDecodedProcess(String name) {
        this.name = name;
    }

    public Map<NodeProcedureType, Map<String, FunctionDefinition>> getFunctions() {
        return this.functions;
    }

    public List<MessageDefinition> getMessages() {
        return this.processMessages;
    }

    private List<VariableDefinition> getReadVariables(BPMNTranslationInfo info) {
        return this.processVariables.stream()
                .filter(v -> v.isRead())
                .toList();
    }

    //written variables are automatically created at global scope
    private List<VariableDefinition> getWrittenVariables(BPMNTranslationInfo info) {
        return this.processVariables.stream()
                .filter(v -> v.isWritten())
                .toList();
    }

    //forced or read but never written
    public List<VariableDefinition> getFreeVariables(BPMNTranslationInfo info) {
        List<String> writtenVariableNames = getWrittenVariables(info).stream().map(v -> v.getName()).toList();
        return getReadVariables(info).stream()
                .filter(v -> {
                    String wv = "";
                    String[] wvss = v.getName().split("\\.");
                    for (String wvs : wvss) {
                        wv += wvs;
                        if (writtenVariableNames.contains(wv) && !info.getForcedInputVariables().contains(wv)) {
                            return false;
                        }
                    }
                    return true;
                }
                ).toList();
    }

    //not forced and written
    public List<VariableDefinition> getBoundVariables(BPMNTranslationInfo info) {
        //miggliorare per prendere in considerazione anche le dot expressions come per free?
        List<VariableDefinition> writtenVariables = getWrittenVariables(info);
        List<VariableDefinition> boundVariables = new ArrayList<>(writtenVariables);
        return boundVariables.stream().filter(v
                -> !info.getForcedInputVariables().contains(v.getName())
        ).toList();
    }

    public List<String> getStartEventFlowNames() {
        return this.startEventFlowNames;
    }

    public MessageDefinition registerProcessMessage(String name) {
        MessageDefinition g = processMessages.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
        if (g == null) {
            g = new MessageDefinition(name);
            processMessages.add(g);
        }
        return g;
    }

    //registers a new global variable for the process and returns its internal definition
    public VariableDefinition registerProcessVariable(String name, VariableDirection d, String sourceId, String sourceExpression) {
        VariableDefinition g = processVariables.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
        if (g == null) {
            g = new VariableDefinition(name);
            processVariables.add(g);
        }
        if (sourceId != null) {
            g.getUsages(d).add(new VariableDefinition.VariableUsageData(sourceId, sourceExpression));
        }
        return g;
    }

    public VariableDefinition registerProcessVariables(List names, VariableDirection d, String sourceId, String sourceExpression) {
        VariableDefinition v = null;
        for (Object composite_name : names) {
            if (composite_name instanceof List l) {
                composite_name = String.join(".", l);
            }
            v = registerProcessVariable(composite_name.toString(), d, sourceId, sourceExpression);
        }
        return v;
    }

    //creates a new function, assigned to the specified function class, and returns its internal definition
    private FunctionDefinition registerFunction(String name, Code code, Map<String, String> parameters, String returnType, NodeProcedureType type) {
        return registerFunction(name, code, new ArrayList<>(List.of()), parameters, returnType, type);
    }

    //creates a new possibly constrained function to output, assigned to the specified function class, and returns its internal definition
    private FunctionDefinition registerFunction(String name, Code code, List<String> triggers, Map<String, String> parameters, String returnType, NodeProcedureType type) {
        FunctionDefinition f;
        if (!functions.containsKey(type)) {
            functions.put(type, new HashMap<>());
        }
        if (!functions.get(type).containsKey(name)) {
            f = new FunctionDefinition(ToJavaBPMNTranslator.sanitizeName(name), code, triggers, returnType, parameters);
            functions.get(type).put(name, f);
        } else {
            f = functions.get(type).get(name);
            System.err.println("warning: discarding function re-definition: " + name);
        }
        return f;
    }

    //creates a new procedure to output, assigned to the specified function class, and returns its internal definition
    private FunctionDefinition registerProcedure(String name, Code code, Map<String, String> parameters, NodeProcedureType type) {
        if (code == null) {
            code = new Code();
        }
        return registerFunction(name, code, parameters, "void", type);
    }

    public void registerNodeProcedure(FlowNode node, Code code) {
        NodeProcedureType type = switch (node) {
            case Gateway g ->
                NodeProcedureType.GATEWAY;
            case Task t ->
                NodeProcedureType.TASK;
            case Event e ->
                NodeProcedureType.EVENT;
            default ->
                NodeProcedureType.FLOW;
        };
        //TODO should be independent from ToJavaBPMNTranslator
        FunctionDefinition p = registerProcedure(getFlowName(node), code, Map.of("s", ToJavaBPMNTranslator.EXECUTILEXPRESSION + ".ProcessStatus"), type);
        registerDecodedNode(node, p);

    }

    public void registerStartEventFlowName(String name) {
        this.startEventFlowNames.add(name);
    }

    public void registerDecodedEdge(FlowNode source, FlowNode target) {
        registerDecodedNode(source);
        registerDecodedNode(target);
        processGraphMap.get(source).getOutgoingEdges().add(target);
    }

    public void registerDecodedNode(FlowNode node) {
        if (!processGraphMap.containsKey(node)) {
            processGraphMap.put(node, new FlowNodeInfo(null));
        }
    }

    public void registerDecodedNode(FlowNode node, FunctionDefinition p) {
        registerDecodedNode(node);
        processGraphMap.get(node).setGeneratedProcedure(p);
    }

    public Map<FlowNode, FlowNodeInfo> getGraph() {
        return processGraphMap;
    }

    public String getName() {
        return name;
    }

    public List<BPMNDecodedFlow> getRawFlows() {
        return rawFlows;
    }
}
