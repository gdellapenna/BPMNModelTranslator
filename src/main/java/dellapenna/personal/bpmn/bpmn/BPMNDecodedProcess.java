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
 * @author giuse
 */
public class BPMNDecodedProcess {
    
    public enum NodeProcedureType {
        GATEWAY, EVENT, TASK, FLOW, GETTER, GENERAL
    };
    
    public enum VariableDirection {
        READ, WRITE, READWRITE
    };
    
    private final String name;
    private final List<BPMNDecodedFlow> rawFlows = new ArrayList<>();
    private final Map<NodeProcedureType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final List<VariableDefinition> processVariables = new ArrayList<>();

    //private final Map<String, Map<VariableDirection, List<String>>> variableUsages = new HashMap<>();
    private final List<String> startEventFlowNames = new ArrayList<>();
    
    public String getFlowName(FlowNode start) {
        //return "flow_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
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
        return type.toString() + "_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
    }
    
    public BPMNDecodedProcess(String name) {
        this.name = name;
        //this.processVariables.put(VariableDirection.READ, new HashMap<>());
        //this.processVariables.put(VariableDirection.WRITE, new HashMap<>());
    }
    
    public Map<NodeProcedureType, Map<String, FunctionDefinition>> getFunctions() {
        return this.functions;
    }
    
    public List<VariableDefinition> getReadVariables() {
        return this.processVariables.stream()
                .filter(v -> v.isRead())
                .toList();
    }

    //written variables are automatically created at global scope
    public List<VariableDefinition> getWrittenVariables() {
        return this.processVariables.stream()
                .filter(v -> v.isWritten())
                .toList();
    }

    //read but never written
    public List<VariableDefinition> getFreeVariables() {
        List<String> writtenVariableNames = getWrittenVariables().stream().map(v -> v.getName()).toList();
        return getReadVariables().stream()
                .filter(v -> {
                    String wv = "";
                    String[] wvss = v.getName().split("\\.");
                    for (String wvs : wvss) {
                        wv += wvs;
                        if (writtenVariableNames.contains(wv)) {
                            return false;
                        }
                    }
                    return true;
                }
                ).toList();
        
    }
    
    public List<String> getStartEventFlowNames() {
        return this.startEventFlowNames;
    }

//    public Map<String, VariableDefinition> getInputs() {
//        return this.inputs;
//    }
    ////
    //registers a new global variable for the process and returns its internal definition
    public VariableDefinition registerProcessVariable(String name, VariableDirection d, String whereUsed) {
        VariableDefinition g = processVariables.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
        if (g == null) {
            g = new VariableDefinition(name);
            processVariables.add(g);
        }        
        if (whereUsed != null) {
            g.getUsages(d).add(whereUsed);
        }
        return g;
    }
    
    public VariableDefinition registerProcessVariables(List names, VariableDirection d, String whereUsed) {
        VariableDefinition v = null;
        for (Object composite_name : names) {
            if (composite_name instanceof List l) {
                composite_name = String.join(".", l);
            }
            v = registerProcessVariable(composite_name.toString(), d, whereUsed);
        }
        return v;
    }

//    private void registerVariableUsage(Object name, VariableDirection d, String where) {
//        if (name instanceof List l) {
//            name = String.join(".", l);
//        }
//        VariableDefinition g;
//        Map<String, VariableDefinition> t = processVariables.get(d);
//        
//        Map<VariableDirection, List<String>> g;
//        if (!variableUsages.containsKey(name.toString())) {
//            g = new HashMap<>();
//            variableUsages.put(name.toString(), g);
//        } else {
//            g = variableUsages.get(name.toString());
//        }
//        List<String> h;
//        if (!g.containsKey(d)) {
//            h = new ArrayList<>();
//            g.put(d, h);
//        } else {
//            h = g.get(d);
//        }
//        h.add(where);
//    }
    //registers a new input variable (from start events or user tasks)
//    public VariableDefinition registerInputVariable(String name) {
//        VariableDefinition i;
//        if (!inputs.containsKey(name)) {
//            i = new VariableDefinition(name);
//            inputs.put(name, i);
//        } else {
//            i = inputs.get(name);
//        }
//        return i;
//    }
    //creates a new function to output, assigned to the specified function class, and returns its internal definition
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
        //code.prepend("//[node] "+node.getId()+((node.getName()!=null && !node.getName().isBlank())?(" - "+node.getName()):""));
        registerProcedure(getFlowName(node), code, Map.of("s", "BPMNExecProcessUtils.ProcessStatus"), type);
    }
    
    public void registerStartEventFlowName(String name) {
        this.startEventFlowNames.add(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the rawFlows
     */
    public List<BPMNDecodedFlow> getRawFlows() {
        return rawFlows;
    }
}
