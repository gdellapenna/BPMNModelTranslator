package dellapenna.personal.bpmn.feel;

import java.util.ArrayList;
import java.util.List;
import org.camunda.feel.syntaxtree.Exp;
import org.camunda.feel.syntaxtree.FunctionParameters;
import scala.collection.Iterator;
import java.math.BigDecimal;
import org.camunda.feel.FeelEngine;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.ParseResult;

/**
 *
 * @author giuse
 * @param <T>
 */
public abstract class AbstractFeelTranslator<T> implements FeelTranslator<T> {

    private final FeelEngineApi api;

    public AbstractFeelTranslator() {
        FeelEngine engine = new FeelEngine.Builder().build();
        api = new FeelEngineApi(engine);
    }

    private List<T> translateFunctionParameters(FunctionParameters e) throws FeelTranslatorException {
        List<T> result = new ArrayList<>();
        switch (e) {
            case org.camunda.feel.syntaxtree.PositionalFunctionParameters t -> {
                Iterator<Exp> iparams = t.params().iterator();
                while (iparams.hasNext()) {
                    result.add(translateExp(iparams.next()));
                }
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate function parameters node of type " + e.getClass().getName());
            }
        }
        return result;
    }

    @Override
    public T translateExp(Exp e) throws FeelTranslatorException {
        return translateExp(null, e);
    }

    @Override
    public T translateExp(Exp input, Exp e) throws FeelTranslatorException {
        T result;

        switch (e) {
            case null -> {
                result=null;
            }
            case org.camunda.feel.syntaxtree.GreaterThan t -> {
                result = translateGreaterThan(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.InputGreaterThan t -> {
                result = translateGreaterThan(translateExp(null, input), translateExp(null, t.x()));
            }
            case org.camunda.feel.syntaxtree.LessThan t -> {
                result = translateLessThan(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.InputLessThan t -> {
                result = translateLessThan(translateExp(null, input), translateExp(null, t.x()));
            }
            case org.camunda.feel.syntaxtree.GreaterOrEqual t -> {
                result = translateGreaterOrEqual(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.InputGreaterOrEqual t -> {
                result = translateGreaterOrEqual(translateExp(null, input), translateExp(null, t.x()));
            }
            case org.camunda.feel.syntaxtree.LessOrEqual t -> {
                result = translateLessOrEqual(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.InputLessOrEqual t -> {
                result = translateLessOrEqual(translateExp(null, input), translateExp(null, t.x()));
            }
            case org.camunda.feel.syntaxtree.Equal t -> {
                result = translateEqual(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.InputEqualTo t -> {
                result = translateEqual(translateExp(null, input), translateExp(null, t.x()));
            }
            case org.camunda.feel.syntaxtree.Not t -> {
                result = translateNot(translateExp(input, t.x()));
            }
            case org.camunda.feel.syntaxtree.Conjunction t -> {
                result = translateAnd(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.Disjunction t -> {
                result = translateOr(translateExp(input, t.x()), translateExp(null, t.y()));
            }
            case org.camunda.feel.syntaxtree.ConstNumber t -> {
                result = translateNumber(translateExp(null, input),t.value().bigDecimal());
            }
            case org.camunda.feel.syntaxtree.ConstBool t -> {
                result = translateBoolean(translateExp(null, input),t.value());
            }
            case org.camunda.feel.syntaxtree.ConstString t -> {
                result = translateString(translateExp(null, input),t.value());
            }
            case org.camunda.feel.syntaxtree.ConstRange t -> {
                result = translateConstRange(translateExp(null, t.start().value()), translateExp(null, t.end().value()));
            }
            case org.camunda.feel.syntaxtree.Ref t -> {
                result = translateVariableReference(scala.collection.JavaConverters.asJava(t.names()));
            }
            case org.camunda.feel.syntaxtree.Addition t -> {
                result = translateAddition(translateExp(input, t.x()), translateExp(input, t.y()));
            }
            case org.camunda.feel.syntaxtree.Division t -> {
                result = translateDivision(translateExp(input, t.x()), translateExp(input, t.y()));
            }
            case org.camunda.feel.syntaxtree.Multiplication t -> {
                result = translateMultiplication(translateExp(input, t.x()), translateExp(input, t.y()));
            }
            case org.camunda.feel.syntaxtree.Subtraction t -> {
                result = translateSubtraction(translateExp(input, t.x()), translateExp(input, t.y()));
            }
            case org.camunda.feel.syntaxtree.ArithmeticNegation t -> {
                result = translateNegation(translateExp(input, t.x()));
            }
            case org.camunda.feel.syntaxtree.Exponentiation t -> {
                result = translateExponentiation(translateExp(input, t.x()), translateExp(input, t.y()));
            }
            case org.camunda.feel.syntaxtree.In t -> {
                result = translateInTest(translateExp(input, t.x()), translateExp(input, t.test()));
            }
            case org.camunda.feel.syntaxtree.InputInRange t -> {
                if (input != null) {
                    result = translateInTest(translateExp(null, input), translateExp(input, t.range()));
                } else {
                    //ranges sometimes get parsed as inputinranges...
                    result = translateExp(input, t.range());
                }
            }
            case org.camunda.feel.syntaxtree.FunctionInvocation t -> {
                result = translateFunctionCall(t.function(), translateFunctionParameters(t.params()));
            }
            case org.camunda.feel.syntaxtree.ConstList t -> {
                result = translateConstList(translateExp(null,input),scala.collection.JavaConverters.asJava(t.items()).stream().map(ee -> translateExpChecked(ee)).toList());
            }
            case org.camunda.feel.syntaxtree.PathExpression t -> {
                result = translatePath(t.key(),translateExp(t.path()));
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate expression node of type " + e.getClass().getName());
            }

        }
        return result;
    }

    private T translateExpChecked(Exp e) {
        try {
            return translateExp(e);
        } catch (FeelTranslatorException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T translate(String input, String expression) throws FeelTranslatorException {
        ParseResult input_parsing = input != null ? api.parseExpression(input) : null;
        if (input_parsing == null || input_parsing.isSuccess()) {
            ParseResult expression_parsing = api.parseExpression(expression);
            if (expression_parsing.isSuccess() && (input_parsing == null || input_parsing.isSuccess())) {
                return translateExp(
                        input_parsing != null ? input_parsing.parsedExpression().expression() : null,
                        expression_parsing.parsedExpression().expression());
            } else {
                throw new FeelTranslatorException("Parsing error: " + expression_parsing.failure().message());
            }
        } else {
            throw new FeelTranslatorException("Parsing error: " + input_parsing.failure().message());
        }
    }

    @Override
    public T translateChecked(String input, String expression) {
        try {
            return translate(input, expression);
        } catch (FeelTranslatorException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T translate(String expression) throws FeelTranslatorException {
        return translate(null, expression);
    }

    @Override
    public T translateChecked(String expression) {
        return translateChecked(null, expression);
    }

    protected abstract T translateGreaterThan(T arg1, T arg2);

    protected abstract T translateLessThan(T arg1, T arg2);

    protected abstract T translateGreaterOrEqual(T arg1, T arg2);

    protected abstract T translateLessOrEqual(T arg1, T arg2);

    protected abstract T translateEqual(T arg1, T arg2);

    protected abstract T translateNot(T arg1);

    protected abstract T translateAnd(T arg1, T arg2);

    protected abstract T translateOr(T arg1, T arg2);

    protected abstract T translateNumber(T context, BigDecimal value);

    protected abstract T translateBoolean(T context, boolean value);

    protected abstract T translateString(T context, String value);

    protected abstract T translateConstRange(T context, T arg2);

    protected abstract T translateAddition(T arg1, T arg2);

    protected abstract T translateDivision(T arg1, T arg2);

    protected abstract T translateMultiplication(T arg1, T arg2);

    protected abstract T translateSubtraction(T arg1, T arg2);

    protected abstract T translateNegation(T arg1);

    protected abstract T translateExponentiation(T arg1, T arg2);

    protected abstract T translateInTest(T arg1, T arg2);

    protected abstract T translateFunctionCall(String function, List<T> arguments);

    protected abstract T translateVariableReference(List<String> names);

    protected abstract T translateConstList(T context, List<T> list);
    
    protected abstract T translatePath(String key, T path);

}
