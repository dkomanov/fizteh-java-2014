package ru.fizteh.fivt.students.moskupols.calculator;

import ru.fizteh.fivt.students.moskupols.calculator.lexemes.Operand;
import ru.fizteh.fivt.students.moskupols.calculator.lexemes.Operator;
import ru.fizteh.fivt.students.moskupols.calculator.lexemes.ParenthesisCloseOperator;
import ru.fizteh.fivt.students.moskupols.calculator.lexemes.ParenthesisOpenOperator;

import java.io.IOError;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by moskupols on 17.09.14.
 */
class Calculator {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();

        try {
            double result = calculate(line);
            if (result - (int)result < 1e-9)
                System.out.println((int)result);
            else
                System.out.println(result);
        } catch (IOError e) {
            System.err.println("Error while printing to stdout: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static double calculate(String expression) throws Exception {
        ArrayList<String> tokens = tokenize(expression);
        tokens.add(0, "(");
        tokens.add(")");
        Stack<Operator> operators = new Stack<Operator>();
        Stack<Operand> operands = new Stack<Operand>();
        boolean emptyParentheses = false;
        for (String t : tokens) {
            Operator op = null;
            Operand operand = null;
            try {
                op = Operator.fromString(t, emptyParentheses);
            } catch (Exception e) {
                operand = Operand.valueOf(t);
            }
            if (op != null) {
                if (op instanceof ParenthesisOpenOperator) {
                    emptyParentheses = true;
                }
                else if (emptyParentheses && op instanceof ParenthesisCloseOperator)
                    throw new Exception("Empty parentheses");
                else {
                    emptyParentheses = false;
                }

                op.affectExpression(operands, operators);
            }
            else {
                operands.push(operand);
                emptyParentheses = false;
            }
        }
        if (!operators.isEmpty())
            throw new Exception("Opening parenthesis '" + operators.peek().toString() + "' has no complement");
        if (operands.size() > 1)
            throw new Exception("Some of the numbers are not used in operations");
        if (operands.isEmpty())
            throw new Exception("There are no numbers in the given string");
        return operands.peek().value;
    }

    private static ArrayList<String> tokenize(String expression) {
        StringTokenizer tokenizer = new StringTokenizer(expression, "()+-/* ", true);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            if (!t.equals(" "))
                tokens.add(t);
        }
        return tokens;
    }
}
