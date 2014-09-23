package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

/**
 * Created by moskupols on 23.09.14.
 */
final public class BinaryProductOperator extends BinaryOperator {
    @Override
    public int priority() {
        return 10;
    }

    @Override
    public Operand apply(Operand left, Operand right) throws Exception {
        return new Operand(left.value.multiply(right.value));
    }

    @Override
    public String toString() {
        return "*";
    }
}
