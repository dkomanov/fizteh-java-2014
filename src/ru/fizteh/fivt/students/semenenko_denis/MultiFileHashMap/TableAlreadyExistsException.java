package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

public class TableAlreadyExistsException extends RuntimeException {

    public TableAlreadyExistsException() {
        super("Can't create table: it exists.");
    }

    public TableAlreadyExistsException(String message) {
        super(message);
    }

    public TableAlreadyExistsException(String message, Throwable ex) {
        super(message, ex);
    }
}
