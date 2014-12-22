package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

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
