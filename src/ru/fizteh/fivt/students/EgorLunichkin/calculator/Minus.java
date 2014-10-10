package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.math.BigDecimal;
import java.util.Stack;

public class Minus extends Operator {
    public byte priority() {
        return 0;
    }

    public void operate(Stack<Operand> nums) throws CalculatorException {
        try {
            BigDecimal first = nums.pop().value;
            BigDecimal second = nums.pop().value;
            nums.push(new Operand(second.subtract(first)));
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
