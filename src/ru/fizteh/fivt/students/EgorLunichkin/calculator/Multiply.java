package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.util.Stack;

public class Multiply extends Operator {
    public byte priority() {
        return 1;
    }

    public void operate (Stack<Operand> nums) throws CalculatorException {
        try {
            nums.push(new Operand(nums.pop().value.multiply(nums.pop().value)));
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
