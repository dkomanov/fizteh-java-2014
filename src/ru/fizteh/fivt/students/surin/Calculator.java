package ru.fizteh.fivt.students.surin;

import java.text.ParseException;
import java.util.ArrayList;

public class Calculator {
    private enum  Operator{
        PLUS('+', 0, (double a, double b) -> a + b),
        MULT('*', 1, (double a, double b) -> a * b),
        MINUS('-', 0, (double a, double b) -> a - b),
        DIV('/', 1, (double a, double b) -> a / b);

        private char sym;
        private final int priority;
        private final Function operator;

        private Operator(char sym, int priority, Function operator) {
            this.sym = sym;
            this.priority = priority;
            this.operator = operator;
        }

        @FunctionalInterface
        private interface Function {
            double apply(double a, double b);
        }
        public int getPriority() {
            return priority;
        }

        public char getSymbol() {
            return sym;
        }

        public double apply(double a, double b) {
            return operator.apply(a, b);
        }
        public static final char[] SYMBOLS;
        public static final int MAXPRIORITY;
        public static final int MINPRIORITY;

        static {
            int n = Operator.values().length;
            SYMBOLS = new char[n];
            int mxp = Integer.MIN_VALUE;
            int mnp = Integer.MAX_VALUE;
            for (int i = 0; i < SYMBOLS.length; i++) {
                SYMBOLS[i] = Operator.values()[i].getSymbol();
                mxp = Math.max(mxp, Operator.values()[i].getPriority());
                mnp = Math.min(mnp, Operator.values()[i].getPriority());
            }
            MAXPRIORITY = mxp;
            MINPRIORITY = mnp;
        }

        public static boolean isOperator(char x) {
            for (char c: SYMBOLS) {
                if (x == c) {
                    return true;
                }
            }
            return false;
        }

        public static Operator getOperator(char x) {
            for (Operator i: values()) {
                if (i.getSymbol() == x) {
                    return i;
                }
            }
            return null;
        }
    }

    boolean bracket(char x) {
        return x == '(' || x == ')';
    }

    ArrayList<String> split(String s) {
        ArrayList<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            boolean cut = false;
            if (Operator.getOperator(s.charAt(i)) != null
                    || bracket(s.charAt(i)) || s.charAt(i) == ' ') {
                cut = true;
            }
            if (i != 0 && (Operator.getOperator(s.charAt(i - 1)) != null
                    || bracket(s.charAt(i - 1)) || s.charAt(i) == ' ')) {
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

    int jump(ArrayList<String> expr, int pos, int priority, int r) throws ParseException {
        int bal = 0;
        for (int i = pos; i < r; i++) {
            if (expr.get(i).equals("(")) {
                bal++;
            }
            if (expr.get(i).equals(")")) {
                bal--;
            }
            if (bal < 0) {
                throw new ParseException("extra )", pos);
            }
            Operator cur = Operator.getOperator(expr.get(i).charAt(0));
            if (bal == 0 && cur != null && cur.getPriority() == priority) {
                return i;
            }
        }
        return r;
    }

    double calcAtom(ArrayList<String> expr, int l, int r) throws ParseException {
        if (l + 1 != r) {
            if (!expr.get(l).equals("(")) {
                throw new ParseException("invalid token", l);
            }
            if (!expr.get(r - 1).equals(")")) {
                throw new ParseException("invalid token", r - 1);
            }
            return calcExpr(expr, l + 1, r - 1, Operator.MINPRIORITY);
        }
        try {
            return Double.valueOf(expr.get(l));
        } catch (NumberFormatException e) {
            throw new ParseException("invalid token", l);
        }
    }

    double calcExpr(ArrayList<String> expr, int l, int r, int priority) throws ParseException {
        if (priority == Operator.MAXPRIORITY + 1) {
            return calcAtom(expr, l, r);
        }
        int i = l;
        double res = 0;
        if (Operator.getOperator(expr.get(l).charAt(0)) == null) {
            int next = jump(expr, i, priority, r);
            res = calcExpr(expr, i, next, priority + 1);
            i = next;
        }
        while (i < r) {
            int next = jump(expr, i + 1, priority, r);
            res = Operator.getOperator(expr.get(i).charAt(0)).apply(res, calcExpr(expr, i + 1, next, priority + 1));
            i = next;
        }
        return res;
    }

    public double run(String arg) throws ParseException {
        ArrayList<String> tk = split(arg);
        return calcExpr(tk, 0, tk.size(), Operator.MINPRIORITY);
    }

    public static void main(String[] args) throws ParseException {
        if (args.length != 1) {
            System.exit(2);
        }
        System.out.format("%f", (new Calculator()).run(args[0]));
    }

}
