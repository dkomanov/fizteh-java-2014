package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.util.Stack;

public abstract class Operator extends Element {
    public abstract byte priority();

    public abstract void operate(Stack<Operand> nums) throws CalculatorException;

    public void pushElement (Stack<Operand> nums, Stack<Operator> ops) throws CalculatorException {
        try {
            while (ops.peek().priority() >= this.priority()) {
                Operator cur_op = ops.pop();
                cur_op.operate(nums);
            }
            ops.push(this);
        } catch (Exception e) {
            throw new CalculatorException(e.getMessage());
        }
    }
}
