package ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions;

/*
* The exception is thrown
* when input database has somehow
* wrong format(extra files, absense of signature file, etc.).
 */
public class DatabaseFormatException extends IllegalArgumentException {
    public DatabaseFormatException(String message) {
        super(message);
    }

    public DatabaseFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseFormatException(Throwable cause) {
        super(cause);
    }
}
