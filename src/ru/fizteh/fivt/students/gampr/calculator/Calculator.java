package ru.fizteh.fivt.students.gampr.calculator;

import java.util.Stack;

public class Calculator {
    String expr;

    Calculator(String expr) {
        this.expr = expr;
    }

    // Проверяем, явлеется ли строка числом
    boolean isDouble(String num) {
        int countComma = 0;
        for (int iChar = 0; iChar < num.length(); iChar ++) {
            if (!(num.charAt(iChar) <= '9' && num.charAt(iChar) >= '0')) {
                if (
                    !(iChar == 0 || iChar == num.length() - 1)      // последний и первый символ не могут быть запятыми
                    && num.charAt(iChar) == '.'
                    && countComma == 0
                ) {                            // запятая только одна
                    countComma++;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /*
        Удаляем все ведущие пробелы
     */
    String deleteSpace(String expr) {
        if (!expr.isEmpty() && expr.charAt(0) == ' ') {
            return expr.replaceFirst("\\s+", "");
        }
        return expr;
    }

    /*
        Возвращает либо операцию, либо скобку, либо число (но в виде строки)
     */
    String getToken(String expr) {
        if ("+-*/()".contains(expr.substring(0, 1))) {
            return expr.substring(0, 1);
        }
        return expr.split("[\\+\\-\\*/() ]+")[0];
    }

    /*
        Вычисляем арифмитическое выражение при помощи стандартного
        алгоритма на двух стэках.
        В одном хранятся числа, а в другом операции(скобки тоже считаются)
     */
    Double calc() {
        Stack<Double> res = new Stack<>();
        Stack<Operation> oper = new Stack<>();
        Boolean operPred = true;
        while (!expr.isEmpty()) {
            expr = deleteSpace(expr);
            String token = getToken(expr);
            expr = expr.substring(token.length());
            if ("+-*/()".contains(token)) {
                Operation op = new Operation(token);
                switch (token) {
                    case ("("):
                        oper.push(op);
                        break;
                    case (")"):
                        operPred = false;
                        while (!oper.empty() && !oper.peek().getType().equals("(")) {
                            oper.pop().apply(res);
                        }
                        if (oper.empty()) {
                            System.err.println("Count brackets is wrong");
                            System.exit(1);
                        }
                        oper.pop();
                        break;
                    case ("-"):
                        if (operPred) {
                            op = new Operation("0-");
                            oper.push(op);
                            break;
                        }
                    case ("+"):
                        if (!operPred) {
                            while (!oper.empty() && !oper.peek().getType().equals("(")) {
                                oper.pop().apply(res);
                            }
                            oper.push(op);
                        }
                        break;
                    case ("/"):
                    case ("*"):
                        if (operPred) {
                            System.err.println("Bad expression");
                            System.exit(1);
                        }
                        while (!oper.empty()
                                && (oper.peek().getType().equals("*") || oper.peek().getType().equals("/"))) {
                            oper.pop().apply(res);
                        }
                        oper.push(op);
                        break;
                    default:
                }
                if ("+-*/(".contains(token)) {
                    operPred = true;
                }
            } else {
                if (!isDouble(token)) {
                    System.err.println("There is bad number");
                    System.exit(1);
                }
                res.push(Double.parseDouble(token));
                operPred = false;
            }
        }
        // Выполняем все операции, что были сохранены в стэк операций
        while (!oper.empty()) {
            oper.pop().apply(res);
        }
        // Результат должен оказаться на вершине стэка
        if (res.size() != 1) {
            System.err.println("It's not right arithmetic expression");
            System.exit(1);
        }
        return res.pop();
    }
}
