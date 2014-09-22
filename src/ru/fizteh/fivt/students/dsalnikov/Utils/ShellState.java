package ru.fizteh.fivt.students.dsalnikov.Utils;


public class ShellState {
    String state;
    Object object;

    public ShellState() {
        state = System.getProperty("user.home");
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
