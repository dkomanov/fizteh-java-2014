package ru.fizteh.fivt.students.sautin1.junit.shell;

/**
 * Exception which is thrown when user desires to exit shell.
 * Created by sautin1 on 10/5/14.
 */
public class UserInterruptException extends Exception {
    public UserInterruptException() {
        super();
    }

    public UserInterruptException(String message) {
        super(message);
    }

    public UserInterruptException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInterruptException(Throwable cause) {
        super(cause);
    }
}
