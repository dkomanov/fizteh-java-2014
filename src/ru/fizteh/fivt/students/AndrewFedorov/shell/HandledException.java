package ru.fizteh.fivt.students.AndrewFedorov.shell;

/**
 * This exception wraps another exception that was caught and handled by a command.
 * @author phoenix
 *
 */
public class HandledException extends RuntimeException {
    private static final long serialVersionUID = 1707144664039960789L;

    public HandledException (String message, Throwable cause) {
	super (message, cause);
    }
    
    public HandledException (Throwable cause) {
	super (cause);
    }
}
