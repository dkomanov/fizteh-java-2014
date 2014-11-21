package ru.fizteh.fivt.students.standy66_new.exceptions;

/**
 * @author andrew
 *         Created by andrew on 21.11.14.
 */
public class CheckedExceptionCaughtException extends RuntimeException {
    public CheckedExceptionCaughtException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckedExceptionCaughtException(Throwable cause) {
        super(cause);
    }
}
