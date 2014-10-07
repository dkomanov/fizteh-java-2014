package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.util.Stack;

public class UnaryMinus extends Operator {
    public byte priority() {
        return 2;
    }

    public void operate(Stack<Operand> nums) throws CalculatorException {
        try {
            nums.push(new Operand(nums.pop().value.negate()));
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
