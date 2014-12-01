package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
public abstract class UnaryOperator extends Operator {

    protected abstract Operand apply(Operand o);

    @Override
    public final void apply(Stack<Operand> operands) throws Exception {
        try {
            Operand o = operands.pop();
            operands.push(apply(o));
        } catch (EmptyStackException e) {
            throw new Exception("Not enough arguments for unary operation " + this.toString());
        }
    }
}
