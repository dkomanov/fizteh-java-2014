package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
public final class ParenthesisOpenOperator extends Operator {
    public final String type;

    public ParenthesisOpenOperator(String type) {
        this.type = type;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void apply(Stack<Operand> operands) throws Exception {
        throw new Exception("Parser logic error: opening parenthesis should never be applied");
    }

    @Override
    public void affectExpression(Stack<Operand> operands, Stack<Operator> operators) throws Exception {
        operators.push(this);
    }

    @Override
    public String toString() {
        return "(";
    }
}
