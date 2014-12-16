package ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions;

public class ProhibitedAccessException extends Exception {
    public ProhibitedAccessException() {
        super();
    }

    public ProhibitedAccessException(final String message) {
        super(message);
    }
}
