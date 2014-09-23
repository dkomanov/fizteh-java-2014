package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by moskupols on 23.09.14.
 */
abstract public class BinaryOperator extends Operator {

    protected abstract Operand apply(Operand left, Operand right) throws Exception;

    @Override
    public final void apply(Stack<Operand> operands) throws Exception {
        try {
            Operand right = operands.pop();
            Operand left = operands.pop();
            operands.push(apply(left, right));
        } catch (EmptyStackException e) {
            throw new Exception("Not enough arguments for binary operation " + this.toString());
        }
    }
}
