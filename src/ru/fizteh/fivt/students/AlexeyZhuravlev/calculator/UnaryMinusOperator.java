package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public final class UnaryMinusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        try {
            NumberLexeme item = results.pop();
            results.push(new NumberLexeme(-item.value));
        } catch (EmptyStackException e) {
            throw new Exception("No argument for unary minus operation");
        }
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception {
        operations.push(this);
    }
}
