package ru.fizteh.fivt.students.gampr.calculator;

import java.util.Stack;

public class Operation {
    String type;

    Operation(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void apply(Stack<Double> st) {
        Double mem1 = st.pop();
        Double mem2 = 0.0;
        if ("+-/*".contains(type.substring(0))) {
            mem2 = st.pop();
        }
        Double res = 0.0;
        switch (type) {
            case "+":
                res = mem1 + mem2;
                break;
            case "0-":
                res = -mem1;
                break;
            case "-":
                res = mem1 - mem2;
                break;
            case "*":
                res = mem1 * mem2;
                break;
            case "/":
                if (mem2 == 0) {
                    System.err.println("Division by zero");
                    System.exit(1);
                }
                res = mem1 / mem2;
                break;
            default:
                System.err.println("Something is wrong");
                System.exit(1);
        }
        st.push(res);
    }
}
