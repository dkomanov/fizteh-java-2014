package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Stack;

public class Divide extends Operator {
    public byte priority() {
        return 1;
    }

    public void operate(Stack<Operand> nums) throws CalculatorException {
        try {
            BigDecimal first = nums.pop().value;
            BigDecimal second = nums.pop().value;
            nums.push(new Operand(second.divide(first, MathContext.DECIMAL128)));
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
