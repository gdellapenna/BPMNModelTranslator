package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author giuse
 */
public class BPMNDecodedProcess {

    private String name;
    private List<BPMNDecodedFlow> rawFlows = new ArrayList<>();
    private final Map<Code.ProcType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final Map<String, GlobalVariableDefinition> globals = new HashMap<>();

    public BPMNDecodedProcess(String name) {
        this.name = name;
    }

    public Map<Code.ProcType, Map<String, FunctionDefinition>> getFunctions() {
        return this.functions;
    }

    public Map<String, GlobalVariableDefinition> getVariables() {
        return this.globals;
    }

    ////
    //registers a new global class variable for the process and returns its internal definition
    public GlobalVariableDefinition registerProcessVariable(String name) {
        GlobalVariableDefinition g;
        if (!globals.containsKey(name)) {
            g = new GlobalVariableDefinition(name);
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

    public void registerFlow(String name, Code code) {
        registerProcedure(name, code, Code.ProcType.FLOW);
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
