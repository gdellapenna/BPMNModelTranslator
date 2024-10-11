package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author giuse
 * @param <T>
 */
public class Code<T> {

    public enum ProcType {
        EVENT, TASK, FLOW, GETTER, GENERAL
    };

    private final List<T> statements = new ArrayList<>();
    private final Map<ProcType, Map<String, FunctionDefinition>> functions = new HashMap<>();
    private final Map<String, GlobalVariableDefinition> globals = new HashMap<>();

    public Code() {
        for (ProcType t : ProcType.values()) {
            functions.put(t, new HashMap<>());
        }
    }

    public Code(List<T> statements) {
        this();
        this.statements.clear();
        this.statements.addAll(statements);
    }

    public Code(T... statements) {
        this();
        Collections.addAll(this.statements, statements);
    }

    public List<T> getStatements() {
        return statements;
    }

    public Map<ProcType, Map<String, FunctionDefinition>> getFunctions() {
        return functions;
    }

    public Map<String, GlobalVariableDefinition> getGlobals() {
        return globals;
    }

    public void append(List<T> statements) {
        statements.addAll(statements);
    }

    public void append(Code<T> code) {
        statements.addAll(code.statements);
        globals.putAll(code.globals);
        for(ProcType pt : ProcType.values()) {
            functions.get(pt).putAll(code.functions.get(pt));
        }
    }

    public void append(T... statements) {
        Collections.addAll(this.statements, statements);
    }

    public void prepend(T statement) {
        this.statements.add(0, statement);
    }

    public boolean isEmpty() {
        return statements.isEmpty();
    }

    ////
    //registers a new global class variable for the process and returns its internal definition
    public GlobalVariableDefinition registerGlobalVariable(String name) {
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
    public FunctionDefinition registerFunction(String name, Code<String> code, Class returnType, ProcType type) {
        return registerFunction(name, code, new ArrayList<>(List.of()), returnType, type);
    }

    //creates a new possibly constrained function to output, assigned to the specified function class, and returns its internal definition
    public FunctionDefinition registerFunction(String name, Code<String> code, List<String> triggers, Class returnType, ProcType type) {
        FunctionDefinition f;
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
    public FunctionDefinition registerProcedure(String name, Code<String> code, ProcType type) {
        if (code == null || code.getStatements().isEmpty()) {
            code = new Code("System.out.println(\"" + name + "\")");
        }
        return registerFunction(name, code, Void.class, type);
    }
}
