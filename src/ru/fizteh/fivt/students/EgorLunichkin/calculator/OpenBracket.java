package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.util.Stack;

public class OpenBracket extends Operator {
    public byte priority() {
        return -1;
    }

    public void operate(Stack<Operand> nums) throws CalculatorException {
        throw new CalculatorException("Operation with opening bracket");
    }

    public void pushElement(Stack<Operand> nums, Stack<Operator> ops) throws CalculatorException {
        ops.push(this);
    }
}
