package ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions;

public class DatabaseReadErrorException extends Exception {
    public DatabaseReadErrorException() {
        super();
    }

    public DatabaseReadErrorException(final String message) {
        super(message);
    }
}
