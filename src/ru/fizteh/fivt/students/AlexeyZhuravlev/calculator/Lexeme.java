package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

abstract public class Lexeme {
    static public Lexeme fromString(String s) throws Exception
    {
        if (s.length() < 1)
            throw new Exception("Empty string is not a lexeme");
        switch(s.charAt(0)) {
            case '+': return new PlusOperator();
            case '-': return new BinaryMinusOperator();
            case '*': return new MultiplyOperator();
            case '/': return new DivideOperator();
            case '~': return new UnaryMinusOperator();
            case '(': return new OpenParenthesisOperator();
            case ')': return new CloseParenthesisOperator();
            default:
                try {
                    return new NumberLexeme(s);
                } catch (NumberFormatException e) {
                    throw new Exception(s + " - incorrect lexeme");
                }
        }
    }

    abstract protected int priority() throws Exception;

    protected abstract void make_operation(Stack<NumberLexeme> results) throws Exception;

    public void add_lexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception
    {
        try {
            while (operations.peek().priority() >= this.priority()) {
                Lexeme operation = operations.pop();
                operation.make_operation(results);
            }
            operations.push(this);
        } catch (EmptyStackException e) {
            throw new Exception("No parenthesis balance");
        }
    }

}