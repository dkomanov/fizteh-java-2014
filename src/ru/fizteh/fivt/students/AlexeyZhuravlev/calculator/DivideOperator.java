package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.Stack;
import java.util.EmptyStackException;

/**
 * @author AlexeyZhuravlev
 */

public final class DivideOperator extends Lexeme {

    @Override
    protected int priority() {
        return 2;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            if (second.value == 0)
                throw new Exception("Division by zero");
            results.push(new NumberLexeme(first.value / second.value));
        } catch (EmptyStackException e) {
            throw new Exception("Not enough arguments for divide operation");
        }
    }
}
