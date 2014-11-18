package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String message) {
        super(message);
    }

    public TableNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public TableNotFoundException() {
        super("Table not found.");
    }
}
