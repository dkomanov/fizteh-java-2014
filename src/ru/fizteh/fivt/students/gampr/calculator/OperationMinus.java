package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;
import java.util.Stack;

public class OperationMinus extends Operation {
    public OperationMinus() {
        super('-');
    }

    @Override
    public Stack<Double> apply(Stack<Double> st) throws IOException {
        if (st.size() < 2) {
           throw new IOException("Bad arithmetic expression");
        }
        Double mem2 = st.pop();
        Double mem1 = st.pop();

        st.push(mem1 - mem2);

        return st;
    }
}
