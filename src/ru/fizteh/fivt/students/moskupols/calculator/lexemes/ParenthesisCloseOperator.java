package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
public final class ParenthesisCloseOperator extends Operator {
    private final String type;

    public ParenthesisCloseOperator(String type) {
        this.type = type;
    }

    boolean closes(ParenthesisOpenOperator opener) {
        return opener.type.equals("(") && this.type.equals(")");
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void apply(Stack<Operand> operands) throws Exception {
        throw new Exception("Parser logic error: closing parenthesis should never be applied");
    }

    @Override
    public void affectExpression(Stack<Operand> operands, Stack<Operator> operators) throws Exception {
        try {
            while (!(operators.peek() instanceof ParenthesisOpenOperator)) {
                Operator o = operators.pop();
                o.apply(operands);
            }
        } catch (EmptyStackException e) {
            throw new Exception("Closing parenthesis '" + type + "' has no complement");
        }
        ParenthesisOpenOperator opener = (ParenthesisOpenOperator)operators.peek();
        if (this.closes(opener))
            operators.pop();
        else
            throw new Exception("Closing parenthesis '" + type
                    + "' is not complement to '" + opener.type + "'");
    }

    @Override
    public String toString() {
        return ")";
    }
}
