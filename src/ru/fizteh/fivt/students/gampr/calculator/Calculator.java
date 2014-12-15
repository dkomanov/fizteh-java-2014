package ru.fizteh.fivt.students.gampr.calculator;

import java.util.Stack;

public class Calculator {
    String expr;

    Calculator(String expr) {
        this.expr = expr;
    }

    String getToken(String expr) {
        if (expr.charAt(0) == ' ') {
            expr.replaceFirst("\\s+", "");
        }
        if ("+-*/()".contains(expr.substring(0, 0))) {
            return expr.substring(0, 0);
        }
        String[] tmp = "[\\+\\-\\*/]".split(expr);
        String res = tmp[0];
        for (String s : tmp) {
            System.out.print(s + " || ");
        }
        System.out.println();
        return res;
    }

    Double calc() {
        Stack<Double> res = new Stack<>();
        Stack<Operation> oper = new Stack<>();
        Boolean operPred;
        while (!expr.isEmpty()) {
            String token = getToken(expr);
            expr = expr.substring(token.length());
            if ("+-*/()".contains(token)) {
                operPred = true;
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
                            System.err.println("Bad bracket outcom!");
                            System.exit(1);
                        }
                        oper.pop();
                        break;
                    case ("+"):
                    case ("-"):
                        if (operPred) {

                            break;
                        }
                        while (!oper.empty() && !oper.peek().getType().equals("(")) {
                            oper.pop().apply(res);
                        }
                        oper.push(op);
                        break;
                    case ("*"):
                        while (!oper.empty() && (oper.peek().getType().equals("*") || oper.peek().getType().equals("/"))) {
                            oper.pop().apply(res);
                        }
                        oper.push(op);
                        break;
                    default:
                }
            } else {
                res.push(Double.parseDouble(expr));
                operPred = false;
            }
        }
        return res.pop();
    }
}
