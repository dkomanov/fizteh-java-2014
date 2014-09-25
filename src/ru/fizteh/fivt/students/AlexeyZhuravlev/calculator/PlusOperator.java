package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public class PlusOperator extends Lexeme{
    @Override
    protected int priority()
    {
        return 1;
    }

    @Override
    protected void make_operation(Stack<NumberLexeme> results) throws Exception
    {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            results.push(new NumberLexeme(first.value + second.value));
        } catch (EmptyStackException e) {
            throw new Exception("Not enough arguments for plus operation");
        }
    }
}
