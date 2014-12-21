package ru.fizteh.fivt.students.Volodin_Denis.JUnit.exceptions;

public class WrongQuantityOfArgumentsException extends Exception {
    public WrongQuantityOfArgumentsException() {
        super();
    }

    public WrongQuantityOfArgumentsException(final String message) {
        super(message);
    }
}
