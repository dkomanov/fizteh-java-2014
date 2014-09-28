package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.Stack;
import java.math.BigDecimal;

/**
 * @author AlexeyZhuravlev
 */

public final class NumberLexeme extends Lexeme {

    public NumberLexeme(BigDecimal value) {
        this.value = value;
    }

    public NumberLexeme(String s) {
        this.value = new BigDecimal(s);
    }

    @Override
    protected int priority() throws Exception {
        throw new Exception("Logical error: NumberLexeme don't have any priority");
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        throw new Exception("Logical error: NumberLexeme can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception {
        results.push(this);
    }

    public final BigDecimal value;
}
