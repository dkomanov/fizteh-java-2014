package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
public abstract class Operator {

    private static final HashMap<String, Operator> UNARIES;
    private static final HashMap<String, Operator> BINARIES;
    private static final HashMap<String, Operator> PARENTHESES;

    static {
        UNARIES = new HashMap<>();
        UNARIES.put("+", new UnaryPlusOperator());
        UNARIES.put("-", new UnaryMinusOperator());
    }

    static {
        BINARIES = new HashMap<>();
        BINARIES.put("+", new BinaryPlusOperator());
        BINARIES.put("-", new BinaryMinusOperator());
        BINARIES.put("*", new BinaryProductOperator());
        BINARIES.put("/", new BinaryDivideOperator());
    }

    static {
        PARENTHESES = new HashMap<>();
        PARENTHESES.put("(", new ParenthesisOpenOperator("("));

        PARENTHESES.put(")", new ParenthesisCloseOperator(")"));
    }

    public static Operator fromString(String s, boolean unaryPreferred) throws Exception {
        if (unaryPreferred && UNARIES.containsKey(s)) {
            return UNARIES.get(s);
        }
        if (BINARIES.containsKey(s)) {
            return BINARIES.get(s);
        }
        if (PARENTHESES.containsKey(s)) {
            return PARENTHESES.get(s);
        }
        throw new Exception("The given lexeme '" + s + "' is not an operator");
    }

    protected abstract int priority();

    protected abstract void apply(Stack<Operand> operands) throws Exception;

    public void affectExpression(Stack<Operand> operands, Stack<Operator> operators) throws Exception {
        while (!operators.isEmpty() && this.priority() <= operators.peek().priority()) {
            Operator o = operators.pop();
            o.apply(operands);
        }
        operators.push(this);
    }
}
