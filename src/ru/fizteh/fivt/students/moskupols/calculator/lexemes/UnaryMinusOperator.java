package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

/**
 * Created by moskupols on 23.09.14.
 * An unary '-' wrapper.
 */
final public class UnaryMinusOperator extends UnaryOperator {
    @Override
    public int priority() {
        return 15;
    }

    @Override
    public Operand apply(Operand o) {
        return new Operand(o.value.negate());
    }

    @Override
    public String toString() {
        return "-";
    }
}
