package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.math.BigDecimal;

/**
 * Created by moskupols on 23.09.14.
 * A binary '/' wrapper.
 */
final public class BinaryDivideOperator extends BinaryOperator{
    @Override
    public int priority() {
        return 10;
    }

    @Override
    public Operand apply(Operand left, Operand right) throws Exception {
        if (right.value.equals(BigDecimal.ZERO)) {
            throw new Exception("Division by zero");
        }
        return new Operand(left.value.divide(right.value));
    }

    @Override
    public String toString() {
        return "/";
    }
}
