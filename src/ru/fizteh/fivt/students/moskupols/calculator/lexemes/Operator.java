package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
abstract public class Operator {

    private final static HashMap<String, Operator> unaries, binaries, parentheses;

    static {
        unaries = new HashMap<String, Operator>();
        unaries.put("+", new UnaryPlusOperator());
        unaries.put("-", new UnaryMinusOperator());
    }

    static {
        binaries = new HashMap<String, Operator>();
        binaries.put("+", new BinaryPlusOperator());
        binaries.put("-", new BinaryMinusOperator());
        binaries.put("*", new BinaryProductOperator());
        binaries.put("/", new BinaryDivideOperator());
    }

    static {
        parentheses = new HashMap<String, Operator>();
        parentheses.put("(", new ParenthesisOpenOperator("("));

        parentheses.put(")", new ParenthesisCloseOperator(")"));
    }

    static public Operator fromString(String s, boolean unaryPreferred) throws Exception {
        if (unaryPreferred && unaries.containsKey(s))
            return unaries.get(s);
        if (binaries.containsKey(s))
            return binaries.get(s);
        if (parentheses.containsKey(s))
            return parentheses.get(s);
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
