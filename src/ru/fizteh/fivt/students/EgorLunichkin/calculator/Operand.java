package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.math.BigDecimal;
import java.util.Stack;

public class Operand extends Element {
    BigDecimal value;

    public Operand(String val) {
        this.value = new BigDecimal(val);
    }

    public Operand(BigDecimal val) {
        this.value = val;
    }

    public void pushElement(Stack<Operand> nums, Stack<Operator> ops) {
        nums.push(this);
    }
}
