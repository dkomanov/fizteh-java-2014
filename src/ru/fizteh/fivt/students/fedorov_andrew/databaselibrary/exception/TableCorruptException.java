package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

public class TableCorruptException extends DatabaseException {
    private static final long serialVersionUID = -2485134551091027284L;

    public TableCorruptException(String tableName, Throwable cause) {
        this(tableName, null, cause);
    }

    public TableCorruptException(String tableName) {
        this(tableName, null, null);
    }

    public TableCorruptException(String tableName, String message) {
        this(tableName, message, null);
    }

    public TableCorruptException(String tableName, String message, Throwable cause) {
        super(
                "Table " + tableName + " is corrupt" + (message == null ? "" : (": " + message)), cause);
    }
}
