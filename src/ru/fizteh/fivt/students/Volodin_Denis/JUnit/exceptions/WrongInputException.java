package ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions;

public class WrongInputException extends Exception {
    public WrongInputException() {
        super();
    }

    public WrongInputException(final String message) {
        super(message);
    }
}