package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

/**
 * Created by moskupols on 23.09.14.
 * A binary '+' wrapper.
 */
public final class BinaryPlusOperator extends BinaryOperator {
    @Override
    public int priority() {
        return 5;
    }

    @Override
    public Operand apply(Operand left, Operand right) throws Exception {
        return new Operand(left.value.add(right.value));
    }

    @Override
    public String toString() {
        return "+";
    }
}
