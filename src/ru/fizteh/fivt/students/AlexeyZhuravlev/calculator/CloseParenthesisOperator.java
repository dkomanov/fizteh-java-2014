package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public final class CloseParenthesisOperator extends Lexeme {

    @Override
    protected int priority() throws Exception {
        throw new Exception("Logical error: close parenthesis is never checked for priority");
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        throw new Exception("Logical error: close parenthesis can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception {
        try {
            while (operations.peek().priority() != 0) {
                Lexeme operation = operations.pop();
                operation.makeOperation(results);
            }
        } catch (EmptyStackException e) {
            throw new Exception("No parenthesis balance");
        }
        operations.pop();
    }
}
