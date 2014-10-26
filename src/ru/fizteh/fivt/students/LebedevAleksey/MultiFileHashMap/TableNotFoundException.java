package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class TableNotFoundException extends Exception {

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
