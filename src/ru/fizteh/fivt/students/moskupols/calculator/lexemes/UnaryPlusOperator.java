package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

/**
 * Created by moskupols on 23.09.14.
 */
final public class UnaryPlusOperator extends UnaryOperator {
    @Override
    public int priority() {
        return 15;
    }

    @Override
    public Operand apply(Operand o) {
        return o;
    }

    @Override
    public String toString() {
        return "+";
    }
}
