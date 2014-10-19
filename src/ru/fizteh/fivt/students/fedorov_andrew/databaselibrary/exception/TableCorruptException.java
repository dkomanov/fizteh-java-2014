package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

public class TableCorruptException extends DatabaseException {
    private static final long serialVersionUID = -2485134551091027284L;

    public TableCorruptException(String tableName) {
	super("Table " + tableName + " is corrupt");
    }
}
