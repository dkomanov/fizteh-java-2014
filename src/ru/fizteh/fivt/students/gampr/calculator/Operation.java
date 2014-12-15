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

    /*
        Применяем нашу операцию на стэке
     */
    public void apply(Stack<Double> st) {
        Double mem1 = st.pop();
        Double mem2 = 0.0;
        /*
            Если операция бинарная, то нужен второй аргумент
         */
        if ("+-/*".contains(type.substring(0))) {
            mem2 = st.pop();
        }
        Double res = 0.0;
        switch (type) {
            case "+":
                res = mem1 + mem2;
                break;
            case "0-": // Унарный минус
                res = -mem1;
                break;
            case "-":
                res = mem2 - mem1;
                break;
            case "*":
                res = mem1 * mem2;
                break;
            case "/":
                if (mem1 == 0) {
                    System.err.println("Division by zero");
                    System.exit(1);
                }
                res = mem2 / mem1;
                break;
            default:
                System.err.println("Count brackets is wrong");
                System.exit(1);
        }
        // Результат операции помещаем обратно в стэк
        st.push(res);
    }
}
