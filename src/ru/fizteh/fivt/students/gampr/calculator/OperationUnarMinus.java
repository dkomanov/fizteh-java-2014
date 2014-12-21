package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;
import java.util.Stack;

public class OperationUnarMinus extends Operation {
    public OperationUnarMinus() {
        super('~');
    }

    @Override
    public Stack<Double> apply(Stack<Double> st) throws IOException {
        if (st.size() < 1) {
            throw new IOException("Bad arithmetic expression");
        }
        Double mem = st.pop();
        st.push(-mem);

        return st;
    }
}
