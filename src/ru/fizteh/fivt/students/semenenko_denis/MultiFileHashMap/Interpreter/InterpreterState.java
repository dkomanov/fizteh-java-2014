package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter;

/**
 * Created by denny_000 on 02.11.2014.
 */
public class InterpreterState {
    private boolean run = true;

    public boolean exited() {
        return !run;
    }

    public void exit() {
        run = false;
    }
}
