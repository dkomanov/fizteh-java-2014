package ru.fizteh.fivt.students.EgorLunichkin.calculator;

import java.util.Stack;

public abstract class Element {
    public static Element parse(String el) throws CalculatorException {
        if (el.length() == 0)
            throw new CalculatorException("Bad expression");
        switch(el.charAt(0)) {
            case '!':
                return new UnaryMinus();
            case '+':
                return new Plus();
            case '-':
                return new Minus();
            case '*':
                return new Multiply();
            case '/':
                return new Divide();
            case '(':
                return new OpenBracket();
            case ')':
                return new CloseBracket();
            default:
                try {
                    return new Operand(el);
                } catch (Exception e) {
                    throw new CalculatorException(e.getMessage());
                }
        }
    }

    public abstract void pushElement (Stack<Operand> nums, Stack<Operator> ops) throws CalculatorException;
}
