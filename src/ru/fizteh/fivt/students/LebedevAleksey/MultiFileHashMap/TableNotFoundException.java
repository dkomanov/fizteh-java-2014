package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.DatabaseException;

public class TableNotFoundException extends DatabaseException {

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
