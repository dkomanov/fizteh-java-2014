package ru.fizteh.fivt.students.moskupols.calculator.lexemes;

import java.math.BigDecimal;

/**
 * Created by moskupols on 23.09.14.
 */
public class Operand {

    public Operand(BigDecimal value) {
        this.value = value;
    }

    static public Operand valueOf(String s) {
        return new Operand(BigDecimal.valueOf(Double.valueOf(s)));
    }

    public final BigDecimal value;
}
