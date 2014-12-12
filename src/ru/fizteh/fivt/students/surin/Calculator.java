package ru.fizteh.fivt.students.surin;

import java.util.ArrayList;

public class Calculator {
    static Operator[] ops = {
            (double a, double b) -> a + b,
            (double a, double b) -> a * b,
            (double a, double b) -> a - b,
            (double a, double b) -> a / b
    };
    static char[] charTable = {'+', '*', '-', '/'};
    static int[] priority = {0, 1, 0, 1};

    boolean operator(char x) {
        for (char c: charTable) {
            if (x == c) {
                return true;
            }
        }
        return false;
    }

    boolean bracket(char x) {
        return x == '(' || x == ')';
    }

    int getPriority(char x) {
        for (int i = 0; i < charTable.length; i++) {
            if (x == charTable[i]) {
                return priority[i];
            }
        }
        return -1;
    }

    Operator getOperator(char x) {
        for (int i = 0; i < charTable.length; i++) {
            if (x == charTable[i]) {
                return ops[i];
            }
        }
        return null;
    }

    ArrayList<String> getTokens(String s) {
        ArrayList<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            boolean cut = false;
            if (operator(s.charAt(i)) || bracket(s.charAt(i)) || s.charAt(i) == ' ') {
                cut = true;
            }
            if (i != 0 && (operator(s.charAt(i - 1)) || bracket(s.charAt(i - 1)) || s.charAt(i) == ' ')) {
                cut = true;
            }
            if (cut) {
                if (cur.length() > 0 && !cur.toString().equals(" ")) {
                    res.add(cur.toString());
                }
                cur = new StringBuilder();
            }
            cur.append(s.charAt(i));
        }
        if (cur.length() > 0 && !cur.toString().equals(" ")) {
            res.add(cur.toString());
        }
        return res;
    }

    int jump(ArrayList<String> expr, int pos, int priority, int r) {
        int bal = 0;
        for (int i = pos; i < r; i++) {
            if (expr.get(i).equals("(")) {
                bal++;
            }
            if (expr.get(i).equals(")")) {
                bal--;
            }
            if (bal == 0 && getPriority(expr.get(i).charAt(0)) == priority) {
                return i;
            }
            //if (bal < 0) return i;
        }
        return r;
    }

    double calcMult(ArrayList<String> expr, int l, int r) {
        //System.err.println(l + " " + r + " Mult");
        if (l + 1 != r) {
            return calcExpr(expr, l + 1, r - 1);
        }
        return Double.valueOf(expr.get(l));
    }

    double calcAdd(ArrayList<String> expr, int l, int r) {
        //System.err.println(l + " " + r + " Add");
        int i = l;
        double res = 1;
        if (!operator(expr.get(l).charAt(0))) {
            int next = jump(expr, i, 1, r);
            res = calcMult(expr, i, next);
            i = next;
        }
        while (i < r) {
            int next = jump(expr, i + 1, 1, r);
            res = getOperator(expr.get(i).charAt(0)).apply(res, calcMult(expr, i + 1, next));
            i = next;
        }
        return res;
    }

    double calcExpr(ArrayList<String> expr, int l, int r) {
        //System.err.println(l + " " + r + " Expr");
        int i = l;
        double res = 0;
        if (!operator(expr.get(l).charAt(0))) {
            int next = jump(expr, i, 0, r);
            res = calcAdd(expr, i, next);
            i = next;
        }
        while (i < r) {
            int next = jump(expr, i + 1, 0, r);
            res = getOperator(expr.get(i).charAt(0)).apply(res, calcAdd(expr, i + 1, next));
            i = next;
        }
        return res;
    }

    public double run(String arg) {
        ArrayList<String> tk = getTokens(arg);
        return calcExpr(tk, 0, tk.size());
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.exit(2);
        }
        System.out.print((new Calculator()).run(args[0]));
    }

    @FunctionalInterface
    interface Operator {
        double apply(double a, double b);
    }
}
