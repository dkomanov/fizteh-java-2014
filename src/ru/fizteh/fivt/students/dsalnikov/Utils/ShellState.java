package ru.fizteh.fivt.students.dsalnikov.Utils;


import java.io.File;

public class ShellState {
    String state;
    Object object;

    public ShellState() {
        File wd = new File("");
        wd.getAbsoluteFile();
        state = wd.getAbsolutePath();
    }

    public ShellState(String s) {
        state = s;
    }

    public String getState() {
        return state;
    }

    public String setState(String s) {
        return state = s;
    }

}
