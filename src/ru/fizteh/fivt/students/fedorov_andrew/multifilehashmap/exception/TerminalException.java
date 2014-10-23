package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception;

/**
 * This exception wraps another exception that was caught and handled by a
 * command.
 * 
 * @author phoenix
 * 
 */
public class TerminalException extends Exception {
    private static final long serialVersionUID = 1707144664039960789L;

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
