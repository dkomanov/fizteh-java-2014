package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

/**
 * Created by moskupols on 23.09.14.
 */
public class Operand {

    public Operand(Double value) {
        this.value = value;
    }

    static public Operand valueOf(String s) {
        return new Operand(Double.valueOf(s));
    }

    public final double value;
}
