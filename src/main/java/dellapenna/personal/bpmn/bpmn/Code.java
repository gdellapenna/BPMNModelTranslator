package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 * @param <T>
 */
public class Code<T> {

    private final List<T> statements = new ArrayList<>();

    public Code() {
        clear();
    }

    public Code(List<T> statements) {
        this();
        set(statements);
    }

    public Code(T... statements) {
        this();
        set(statements);
    }

    public Code(Code other) {
        this();
        set(other);
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

    public void set(List<T> statements) {
        clear();
        this.statements.addAll(statements);
    }

    public void set(Code code) {
        clear();
        this.statements.addAll(code.statements);
    }

    public void set(T... statements) {
        clear();
        Collections.addAll(this.statements, statements);
    }

    public void clear() {
        this.statements.clear();
    }

    public boolean isEmpty() {
        return this.statements.isEmpty();
    }

}
