package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author giuse
 */
public class Code {

    public enum ProcType {
        EVENT, TASK, FLOW, GETTER, GENERAL
    };

    private final List<String> statements = new ArrayList<>();
    

    public Code() {
    }

    public Code(List<String> statements) {
        this();
        this.statements.clear();
        this.statements.addAll(statements);
    }

    public Code(String... statements) {
        this();
        Collections.addAll(this.statements, statements);
    }

    public List<String> getStatements() {
        return this.statements;
    }

    

    public void append(List<String> statements) {
        this.statements.addAll(statements);
    }

    public void append(Code code) {
        this.statements.addAll(code.statements);
//        this.globals.putAll(code.globals);
//        for(ProcType pt : ProcType.values()) {
//            this.functions.get(pt).putAll(code.functions.get(pt));
//        }
    }

    public void append(String... statements) {
        Collections.addAll(this.statements, statements);
    }

    public void prepend(String statement) {
        this.statements.add(0, statement);
    }

    public boolean isEmpty() {
        return this.statements.isEmpty();
    }

   
}
