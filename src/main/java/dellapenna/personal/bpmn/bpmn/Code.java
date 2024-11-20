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
        GATEWAY, EVENT, TASK, FLOW, GETTER, GENERAL
    };

    private final List<String> statements = new ArrayList<>();

    public Code() {
        this.statements.clear();
    }

    public Code(List<String> statements) {
        this();
        this.statements.addAll(statements);
    }

    public Code(String... statements) {
        this();
        Collections.addAll(this.statements, statements);
    }

    public Code(Code other) {
        this();
        this.statements.addAll(other.statements);
    }

    public List<String> getStatements() {
        return this.statements;
    }

    public void append(List<String> statements) {
        this.statements.addAll(statements);
    }

    public void append(Code code) {
        this.statements.addAll(code.statements);
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
