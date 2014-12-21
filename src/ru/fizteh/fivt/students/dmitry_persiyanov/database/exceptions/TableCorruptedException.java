package ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions;

/**
 * Created by drack3800 on 22.11.2014.
 */
public class TableCorruptedException extends IllegalArgumentException {
    public TableCorruptedException(final String tableName) {
        super("table \"" + tableName + "\" is corrupted");
    }

    public TableCorruptedException() {
        super();
    }

    public TableCorruptedException(String tableName, Throwable cause) {
        super("table \"" + tableName + "\" is corrupted", cause);
    }

    public TableCorruptedException(Throwable cause) {
        super(cause);
    }
}
