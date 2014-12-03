package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

/**
 * This exception wraps another exception that was caught and handled by a command.
 * @author phoenix
 */
public class TerminalException extends Exception {

    public TerminalException(String message) {
        super(message);
    }

    public TerminalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TerminalException(Throwable cause) {
        super(cause);
    }
}
