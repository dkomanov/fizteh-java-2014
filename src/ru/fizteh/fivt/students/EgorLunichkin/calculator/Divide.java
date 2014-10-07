package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.math.BigDecimal;
import java.util.Stack;

public class Divide extends Operator {
    public byte priority() {
        return 1;
    }

    public void operate(Stack<Operand> nums) throws CalculatorException {
        try {
            nums.push(new Operand(new BigDecimal(1).divide(nums.pop().value.divide(nums.pop().value))));
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
