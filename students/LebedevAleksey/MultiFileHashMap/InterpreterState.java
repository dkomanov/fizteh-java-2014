package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class InterpreterState {
    private boolean run = true;

    public boolean exited() {
        return !run;
    }

    public void exit() {
        run = false;
    }
}
