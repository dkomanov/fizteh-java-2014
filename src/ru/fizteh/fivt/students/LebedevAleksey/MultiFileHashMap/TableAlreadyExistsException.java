package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class TableAlreadyExistsException extends Exception {

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
