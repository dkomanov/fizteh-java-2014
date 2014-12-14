package ru.fizteh.fivt.students.irina_karatsapova.filemap.utils;

public class DataBaseException extends Exception {
    public DataBaseException() {
        super();
    }
    public DataBaseException(String message) {
        super(message);
    }
    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public DataBaseException(Throwable cause) {
        super(cause);
    }
}
