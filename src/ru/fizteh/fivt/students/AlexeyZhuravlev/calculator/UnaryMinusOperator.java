package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public class UnaryMinusOperator extends Lexeme
{
    @Override
    protected int priority()
    {
        return 3;
    }

    @Override
    protected void make_operation(Stack<NumberLexeme> results) throws Exception
    {
        try {
            NumberLexeme item = results.pop();
            results.push(new NumberLexeme(-item.value));
        } catch (EmptyStackException e) {
            throw new Exception("No argument for unary minus operation");
        }
    }

    @Override
    public void add_lexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception
    {
        operations.push(this);
    }

}
