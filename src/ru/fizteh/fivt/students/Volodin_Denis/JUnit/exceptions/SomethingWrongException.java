package ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions;

public class SomethingWrongException extends Exception {
    public SomethingWrongException() {
        super();
    }

    public SomethingWrongException(final String message) {
        super(message);
    }
}
