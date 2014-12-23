package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;
import java.util.Stack;

// abstract class for operations
public abstract class Operation {
    private Character type;

    public Operation(Character type) {
        this.type = type;
    }

    public Character getType() {
        return type;
    }

    /*
        Применяем нашу операцию на стэке
     */
    public abstract Stack<Double> apply(Stack<Double> st) throws IOException;
}
