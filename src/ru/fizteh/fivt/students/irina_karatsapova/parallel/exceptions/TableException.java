package ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions;

public class TableException extends IllegalStateException {
    public TableException() {
        super();
    }
    public TableException(String message) {
        super(message);
    }
    public TableException(String message, Throwable cause) {
        super(message, cause);
    }
    public TableException(Throwable cause) {
        super(cause);
    }
}
