package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
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

    private String name;
    private List<BPMNDecodedFlow> rawFlows = new ArrayList<>();
    private final Map<Code.ProcType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final Map<String, VariableDefinition> globals = new HashMap<>();
    private final Map<String, VariableDefinition> inputs = new HashMap<>();
    private final List<String> startEventFlowNames = new ArrayList<>();
    
    
    public String getFlowName(FlowNode start) {
        //return "flow_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
        Code.ProcType type = switch (start) {
            case Gateway g ->
                Code.ProcType.GATEWAY;
            case Task t ->
                Code.ProcType.TASK;
            case Event e ->
                Code.ProcType.EVENT;
            default ->
                Code.ProcType.FLOW;
        };
        return type.toString() + "_" + (start.getName() != null && !start.getName().isBlank() ? start.getName() : start.getId());
    }

    public BPMNDecodedProcess(String name) {
        this.name = name;
    }

    public Map<Code.ProcType, Map<String, FunctionDefinition>> getFunctions() {
        return this.functions;
    }

    public Map<String, VariableDefinition> getVariables() {
        return this.globals;
    }

    public List<String> getStartEventFlowNames() {
        return this.startEventFlowNames;
    }

    public Map<String, VariableDefinition> getInputs() {
        return this.inputs;
    }

    ////
    //registers a new global class variable for the process and returns its internal definition
    public VariableDefinition registerProcessVariable(String name) {
        VariableDefinition g;
        if (!globals.containsKey(name)) {
            g = new VariableDefinition(name);
            globals.put(name, g);
        } else {
            g = globals.get(name);
        }
        return g;
    }

    //creates a new function to output, assigned to the specified function class, and returns its internal definition
    public FunctionDefinition registerFunction(String name, Code code, Class returnType, Code.ProcType type) {
        return registerFunction(name, code, new ArrayList<>(List.of()), returnType, type);
    }

    //creates a new possibly constrained function to output, assigned to the specified function class, and returns its internal definition
    public FunctionDefinition registerFunction(String name, Code code, List<String> triggers, Class returnType, Code.ProcType type) {
        FunctionDefinition f;
        if (!functions.containsKey(type)) {
            functions.put(type, new HashMap<>());
        }
        if (!functions.get(type).containsKey(name)) {
            f = new FunctionDefinition(ToJavaBPMNTranslator.sanitizeName(name), code, triggers, returnType, Collections.EMPTY_MAP);
            functions.get(type).put(name, f);
        } else {
            f = functions.get(type).get(name);
            System.err.println("warning: discarding function re-definition: " + name);
        }
        return f;
    }

    //creates a new procedure to output, assigned to the specified function class, and returns its internal definition
    public FunctionDefinition registerProcedure(String name, Code code, Code.ProcType type) {
//        if (code == null || code.getStatements().isEmpty()) {
//            code = new Code("System.out.println(\"" + name + "\")");
//        }
        if (code == null) {
            code = new Code();
        }
        return registerFunction(name, code, Void.class, type);
    }

    public void registerNodeProcedure(String name, Code code) {
        registerProcedure(name, code, Code.ProcType.FLOW);
    }

    public void registerNodeProcedure(FlowNode node, Code code) {
        Code.ProcType type = switch (node) {
            case Gateway g ->
                Code.ProcType.GATEWAY;
            case Task t ->
                Code.ProcType.TASK;
            case Event e ->
                Code.ProcType.EVENT;
            default ->
                Code.ProcType.FLOW;
        };        
        registerProcedure(getFlowName(node), code, type);
    }

    public void registerStartEventFlowName(String name) {
        this.startEventFlowNames.add(name);
    }

    //registers a new input variable (from start events or user tasks)
    public VariableDefinition registerInput(String name) {
        VariableDefinition i;
        if (!inputs.containsKey(name)) {
            i = new VariableDefinition(name);
            inputs.put(name, i);
        } else {
            i = inputs.get(name);
        }
        return i;
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
