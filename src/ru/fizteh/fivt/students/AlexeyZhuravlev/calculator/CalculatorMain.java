package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public class CalculatorMain {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("First argument(expression) expected.");
            System.exit(1);
        }
        try {
            BigDecimal result = calculate(args[0]);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }

    private static BigDecimal calculate(String inputString) throws Exception {
        inputString = "(" + inputString.replaceAll(" ", "") + ")";
        inputString = markUnaryMinuses(inputString, '~');
        StringTokenizer tokenizer = new StringTokenizer(inputString, "+-*/()~", true);
        Stack<NumberLexeme> results = new Stack<NumberLexeme>();
        Stack<Lexeme> operations = new Stack<Lexeme>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Lexeme lex = Lexeme.fromString(t);
            lex.addLexeme(results, operations);
        }
        if (!operations.isEmpty()) {
            throw new Exception("No parenthesis balance");
        }
        if (results.isEmpty()) {
            throw new Exception("No numbers in equations");
        }
        if (results.size() > 1) {
            throw new Exception("Not enough operators for these numbers");
        }
        return results.peek().value;
    }

    private static String markUnaryMinuses(String s, char newSymbol) throws Exception {
        String result = "(";
        String correctSymbols = "01234567890.+-*/()";
        for (int i = 1; i < s.length(); i++) {
            char current = s.charAt(i);
            char previous = s.charAt(i - 1);
            if (correctSymbols.indexOf(current) == -1) {
                throw new Exception("Incorrect symbol in equation");
            }
            if (current == ')' && previous == '(') {
                throw new Exception("Empty parenthesis");
            }
            if (current == '-' && previous != ')' && !Character.isDigit(previous)) {
                current = newSymbol;
            }
            result = result + current;
        }
        return result;
    }
}
