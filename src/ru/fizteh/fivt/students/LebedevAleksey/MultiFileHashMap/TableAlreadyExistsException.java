package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.DatabaseException;

public class TableAlreadyExistsException extends DatabaseException {

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
