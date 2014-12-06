package ru.fizteh.fivt.students.dnovikov.storeable.Exceptions;

public class StopInterpreterException extends RuntimeException {
    public StopInterpreterException(String message) {
        super(message);
    }
    public StopInterpreterException() {
        super();
    }
}
