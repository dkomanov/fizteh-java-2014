package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitException extends Exception {
    public JUnitException(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
