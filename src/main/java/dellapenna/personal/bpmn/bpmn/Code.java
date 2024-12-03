package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author giuse
 */
public class Code<T> {

    public enum ProcType {
        GATEWAY, EVENT, TASK, FLOW, GETTER, GENERAL
    };

    private final List<T> statements = new ArrayList<>();

    public Code() {
        this.statements.clear();
    }

    public Code(List<T> statements) {
        this();
        this.statements.addAll(statements);
    }

    public Code(T... statements) {
        this();
        Collections.addAll(this.statements, statements);
    }

    public Code(Code other) {
        this();
        this.statements.addAll(other.statements);
    }

    public List<T> getStatements() {
        return this.statements;
    }

    public void append(List<T> statements) {
        this.statements.addAll(statements);
    }

    public void append(Code code) {
        this.statements.addAll(code.statements);
    }

    public void append(T... statements) {
        Collections.addAll(this.statements, statements);
    }

    public void prepend(T statement) {
        this.statements.add(0, statement);
    }

    public boolean isEmpty() {
        return this.statements.isEmpty();
    }

}
