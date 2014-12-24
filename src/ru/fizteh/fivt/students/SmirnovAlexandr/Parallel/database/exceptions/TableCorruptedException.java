package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions;

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
