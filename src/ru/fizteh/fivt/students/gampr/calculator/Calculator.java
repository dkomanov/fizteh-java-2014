package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringJoiner;

public class Calculator {
    static String numString;
    static {
        numString = "0123456789.";
    }

    String expr;
    String operString;
    Map<Character, Operation> operations;

    Calculator(String expr, Operation[] operations) {
        this.operations = new HashMap<>();

        StringJoiner joiner = new StringJoiner("");
        for (Operation operat : operations) {
            this.operations.put(operat.getType(), operat);
            joiner.add(Character.toString(operat.getType()));
        }
        operString = joiner.toString();

        this.expr = expr.replaceAll("\\s+", "");
    }

    // Проверяем, явлеется ли строка числом
    boolean isDouble(String num) {
        int countComma = 0;
        for (int iChar = 0; iChar < num.length(); iChar ++) {
            if (!numString.contains(num.substring(iChar, iChar + 1)) || num.charAt(iChar) == '.') {
                if (
                    !(iChar == 0 || iChar == num.length() - 1)      // последний и первый символ не могут быть запятыми
                    && num.charAt(iChar) == '.'
                    && countComma == 0
                ) {                            // запятая только одна
                    countComma ++;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /*
        Возвращает якобы число (но в виде строки)
     */
    String getNumString(String expr) {
        return expr.split("[\\+\\-\\*/() ]+")[0];
    }

    /*
        Вычисляем арифмитическое выражение при помощи стандартного
        алгоритма на двух стэках.
        В одном хранятся числа, а в другом операции(скобки тоже считаются)
     */
    Double calc() throws IOException {
        Stack<Double> res = new Stack<>();
        Stack<Character> oper = new Stack<>();

        boolean operPred = true; // для унарных плюсов
        while (!expr.isEmpty()) {
            if (operations.containsKey(expr.charAt(0)) || expr.charAt(0) == '(' || expr.charAt(0) == ')') {
                char ch = expr.charAt(0);
                expr = expr.substring(1);

                // Сначала обработаем скобки
                if (ch == ')') {
                    while (!oper.empty() && !oper.peek().equals('(')) {
                        res = operations.get(oper.pop()).apply(res);
                    }
                    if (oper.empty() || !oper.peek().equals('(')) {
                        throw new IOException("Count brackets is wrong");
                    }
                    oper.pop();

                    operPred = false;

                    continue;
                }

                // Обработаем унарные плюсы/минусы
                if ((ch == '-' || ch == '+') && operPred) {
                    if (ch == '-') {
                        oper.push('~');
                    }
                    continue;
                }
                if (ch == '+' || ch == '-') {
                    while (!oper.empty() && !oper.peek().equals('(')) {
                        res = operations.get(oper.pop()).apply(res);
                    }
                }
                if (ch == '*' || ch == '/') {
                    while (!oper.empty()
                            && (oper.peek().equals('*') || oper.peek().equals('/'))) {
                        res = operations.get(oper.pop()).apply(res);
                    }
                }

                oper.push(ch);
                operPred = true;
            } else {
                String token = getNumString(expr);
                expr = expr.substring(token.length());

                if (!isDouble(token)) {
                    throw new IOException("There is bad number");
                }
                res.push(Double.parseDouble(token));
                operPred = false;
            }
        }
        // Выполняем все операции, что были сохранены в стэк операций
        while (!oper.empty()) {
            res = operations.get(oper.pop()).apply(res);
        }
        // Результат должен оказаться на вершине стэка
        if (res.size() != 1) {
            throw new IOException("It's not right arithmetic expression");
        }
        return res.pop();
    }
}
