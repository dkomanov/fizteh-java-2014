package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */
public final class OpenParenthesisOperator extends Lexeme {

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        throw new Exception("Logical error: open parenthesis can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception {
        operations.push(this);
    }

}
