package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Exception which is thrown when an error in command executing occurs.
 * Created by sautin1 on 10/5/14.
 */
public class CommandExecuteException extends Exception {
    public CommandExecuteException() {
        super();
    }

    public CommandExecuteException(String message) {
        super(message);
    }

    public CommandExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecuteException(Throwable cause) {
        super(cause);
    }
}
