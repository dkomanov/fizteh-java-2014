package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import java.io.IOException;

/**
 * Exception that indicates that some table is corrupt and cannot be used.
 */
public class TableCorruptIOException extends DatabaseIOException {

    public TableCorruptIOException(String tableName, IOException cause) {
        this(tableName, null, cause);
    }

    public TableCorruptIOException(String tableName) {
        this(tableName, null, null);
    }

    public TableCorruptIOException(String tableName, String message) {
        this(tableName, message, null);
    }

    /**
     * Generally, forms error message like this: Table (tableName) is corrupt[: message]
     */
    public TableCorruptIOException(String tableName, String message, IOException cause) {
        super(
                "Table " + tableName + " is corrupt" + (message == null ? "" : (": " + message)), cause);
    }
}
