package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

/**
 * Exception that occurs while checking just read table for values validity.
 * For inner use.
 */
public class ImproperStoreableException extends RuntimeException {
    public ImproperStoreableException(String message) {
        super(message);
    }

    public ImproperStoreableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImproperStoreableException(Throwable cause) {
        super(cause);
    }
}
