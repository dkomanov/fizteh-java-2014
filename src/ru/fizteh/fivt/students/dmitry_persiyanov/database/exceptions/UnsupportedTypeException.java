package ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions;

/**
 * Created by drack3800 on 23.11.2014.
 */
public class UnsupportedTypeException extends IllegalArgumentException {
    public UnsupportedTypeException() {
        super();
    }

    public UnsupportedTypeException(String s) {
        super(s);
    }

    public UnsupportedTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTypeException(Throwable cause) {
        super(cause);
    }
}
