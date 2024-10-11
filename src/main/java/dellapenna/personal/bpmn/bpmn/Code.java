package dellapenna.personal.bpmn.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author giuse
 * @param <T>
 */
public class Code<T> {

    private final List<T> statements;

    public Code() {
        this.statements = new ArrayList<>();
    }

    public Code(List<T> statements) {
        this.statements = statements;
    }

    public Code(T... statements) {
        this.statements = new ArrayList<>();
        Collections.addAll(this.statements, statements);
    }

    public List<T> getStatements() {
        return statements;
    }

    public void append(List<T> statements) {
        statements.addAll(statements);
    }

    public void append(Code<T> code) {
        statements.addAll(code.statements);
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
}
