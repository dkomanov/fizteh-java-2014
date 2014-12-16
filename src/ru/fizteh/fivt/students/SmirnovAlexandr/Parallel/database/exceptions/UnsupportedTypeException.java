package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions;


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
