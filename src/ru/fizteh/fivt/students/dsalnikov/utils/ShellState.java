package ru.fizteh.fivt.students.dsalnikov.utils;


public class ShellState {
    private String state;

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
