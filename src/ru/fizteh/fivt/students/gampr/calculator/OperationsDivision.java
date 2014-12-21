package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;
import java.util.Stack;

public class OperationsDivision extends Operation{
    public OperationsDivision() {
        super('/');
    }

    @Override
    public Stack<Double> apply(Stack<Double> st) throws IOException {
        if (st.size() < 2) {
            throw new IOException("Bad arithmetic expression");
        }
        Double mem2 = st.pop();
        Double mem1 = st.pop();

        if (mem2.equals(0.0)) {
           throw new IOException("Division by zero");
        }
        st.push(mem1 / mem2);

        return st;
    }
}
