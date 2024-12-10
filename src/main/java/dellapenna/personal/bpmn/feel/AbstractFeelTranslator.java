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
import org.camunda.feel.syntaxtree.OpenConstRangeBoundary;

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

    private List<T> translateFunctionParameters(FunctionParameters e, FeelTranslationInfo info) throws FeelTranslatorException {
        List<T> result = new ArrayList<>();
        switch (e) {
            case org.camunda.feel.syntaxtree.PositionalFunctionParameters t -> {
                Iterator<Exp> iparams = t.params().iterator();
                while (iparams.hasNext()) {
                    result.add(translateExp(iparams.next(), info));
                }
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate function parameters node of type " + e.getClass().getName());
            }
        }
        return result;
    }

//    @Override
    protected T translateExp(Exp e, FeelTranslationInfo info) throws FeelTranslatorException {
        return translateExp(null, e, info);
    }

//    @Override
    protected T translateExp(Exp input, Exp e, FeelTranslationInfo info) throws FeelTranslatorException {
        T result;
        switch (e) {
            case null -> {
                result = null;
            }
            case org.camunda.feel.syntaxtree.GreaterThan t -> {
                result = translateGreaterThan(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputGreaterThan t -> {
                result = translateGreaterThan(translateExp(null, input, info), translateExp(null, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.LessThan t -> {
                result = translateLessThan(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputLessThan t -> {
                result = translateLessThan(translateExp(null, input, info), translateExp(null, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.GreaterOrEqual t -> {
                result = translateGreaterOrEqual(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputGreaterOrEqual t -> {
                result = translateGreaterOrEqual(translateExp(null, input, info), translateExp(null, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.LessOrEqual t -> {
                result = translateLessOrEqual(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputLessOrEqual t -> {
                result = translateLessOrEqual(translateExp(null, input, info), translateExp(null, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.Equal t -> {
                result = translateEqual(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputEqualTo t -> {
                result = translateEqual(translateExp(null, input, info), translateExp(null, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.Not t -> {
                result = translateNot(translateExp(input, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.Conjunction t -> {
                result = translateAnd(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.Disjunction t -> {
                result = translateOr(translateExp(input, t.x(), info), translateExp(null, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.ConstNumber t -> {
                result = translateNumber(translateExp(null, input, info), t.value().bigDecimal(), info);
            }
            case org.camunda.feel.syntaxtree.ConstBool t -> {
                result = translateBoolean(translateExp(null, input, info), t.value(), info);
            }
            case org.camunda.feel.syntaxtree.ConstString t -> {
                result = translateString(translateExp(null, input, info), t.value(), info);
            }
            case org.camunda.feel.syntaxtree.ConstRange t -> {
                result = translateConstRange(translateExp(null, t.start().value(), info), translateExp(null, t.end().value(), info), info);
            }
            case org.camunda.feel.syntaxtree.Ref t -> {
                result = translateVariableReference(scala.collection.JavaConverters.asJava(t.names()), info);
            }
            case org.camunda.feel.syntaxtree.Addition t -> {
                result = translateAddition(translateExp(input, t.x(), info), translateExp(input, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.Division t -> {
                result = translateDivision(translateExp(input, t.x(), info), translateExp(input, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.Multiplication t -> {
                result = translateMultiplication(translateExp(input, t.x(), info), translateExp(input, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.Subtraction t -> {
                result = translateSubtraction(translateExp(input, t.x(), info), translateExp(input, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.ArithmeticNegation t -> {
                result = translateNegation(translateExp(input, t.x(), info), info);
            }
            case org.camunda.feel.syntaxtree.Exponentiation t -> {
                result = translateExponentiation(translateExp(input, t.x(), info), translateExp(input, t.y(), info), info);
            }
            case org.camunda.feel.syntaxtree.In t -> {
                result = translateInTest(translateExp(input, t.x(), info), translateExp(input, t.test(), info), info);
            }
            case org.camunda.feel.syntaxtree.InputInRange t -> {
                if (input != null) {
                    result = translateInTest(translateExp(null, input, info),
                            (t.range().start() instanceof OpenConstRangeBoundary),
                            translateExp(null, t.range().start().value(), info),
                            (t.range().end() instanceof OpenConstRangeBoundary),
                            translateExp(null, t.range().end().value(), info),
                            info);
                } else {
                    //ranges sometimes get parsed as inputinranges...
                    result = translateExp(input, t.range(), info);
                }
            }
            case org.camunda.feel.syntaxtree.FunctionInvocation t -> {
                result = translateFunctionCall(t.function(), translateFunctionParameters(t.params(), info), info);
            }
            case org.camunda.feel.syntaxtree.ConstList t -> {
                result = translateConstList(translateExp(null, input, info), scala.collection.JavaConverters.asJava(t.items()).stream().map(ee -> translateExpChecked(ee, info)).toList(), info);
            }
            case org.camunda.feel.syntaxtree.PathExpression t -> {
                result = translatePath(t.key(), translateExp(t.path(), info), info);
            }
            default -> {
                throw new FeelTranslatorException("Cannot translate expression node of type " + e.getClass().getName());
            }

        }
        return result;
    }

    private T translateExpChecked(Exp e, FeelTranslationInfo info) {
        try {
            return translateExp(e, info);
        } catch (FeelTranslatorException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T translate(String input, String expression, FeelTranslationInfo info) throws FeelTranslatorException {
        Exp input_parsing = input != null ? parse(input) : null;
        //if (input_parsing == null || input_parsing.isSuccess()) {
        Exp expression_parsing = parse(expression);
        //if (expression_parsing.isSuccess() && (input_parsing == null || input_parsing.isSuccess())) {
        return translateExp(input_parsing != null ? input_parsing : null, expression_parsing, info);
        //} else {
        //    throw new FeelTranslatorException("Parsing error: " + expression_parsing.failure().message());
        //}
        //} else {
        //    throw new FeelTranslatorException("Parsing error: " + input_parsing.failure().message());
        //}
    }

    @Override
    public T translateChecked(String input, String expression, FeelTranslationInfo info) {
        try {
            return translate(input, expression, info);
        } catch (FeelTranslatorException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T translate(String expression, FeelTranslationInfo info) throws FeelTranslatorException {
        return translate(null, expression, info);
    }

    @Override
    public T translateChecked(String expression, FeelTranslationInfo info) {
        return translateChecked(null, expression, info);
    }

    @Override
    public Exp parse(String expression) throws FeelTranslatorException {
        ParseResult expression_parsing = api.parseExpression(expression);
        if (expression_parsing.isSuccess()) {
            return expression_parsing.parsedExpression().expression();
        } else {
            throw new FeelTranslatorException("Parsing error: " + expression_parsing.failure().message());
        }

    }

    protected abstract T translateGreaterThan(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateLessThan(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateGreaterOrEqual(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateLessOrEqual(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateEqual(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateNot(T arg1, FeelTranslationInfo info);

    protected abstract T translateAnd(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateOr(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateNumber(T context, BigDecimal value, FeelTranslationInfo info);

    protected abstract T translateBoolean(T context, boolean value, FeelTranslationInfo info);

    protected abstract T translateString(T context, String value, FeelTranslationInfo info);

    protected abstract T translateConstRange(T context, T arg2, FeelTranslationInfo info);

    protected abstract T translateAddition(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateDivision(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateMultiplication(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateSubtraction(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateNegation(T arg1, FeelTranslationInfo info);

    protected abstract T translateExponentiation(T arg1, T arg2, FeelTranslationInfo info);

    protected abstract T translateInTest(T expression, T range, FeelTranslationInfo info);

    protected abstract T translateInTest(T arg1, boolean startOpen, T start, boolean endOpen, T end, FeelTranslationInfo info);

    protected abstract T translateFunctionCall(String function, List<T> arguments, FeelTranslationInfo info);

    protected abstract T translateVariableReference(List<String> names, FeelTranslationInfo info);

    protected abstract T translateConstList(T context, List<T> list, FeelTranslationInfo info);

    protected abstract T translatePath(String key, T path, FeelTranslationInfo info);

}
