package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions;

public class ThreadInterruptException extends IllegalStateException {
    public ThreadInterruptException() {
        super();
    }
    public ThreadInterruptException(String message) {
        super(message);
    }
    public ThreadInterruptException(String message, Throwable cause) {
        super(message, cause);
    }
    public ThreadInterruptException(Throwable cause) {
        super(cause);
    }
}
