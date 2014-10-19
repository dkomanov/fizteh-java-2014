package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

public class NoActiveTableException extends DatabaseException {
    private static final long serialVersionUID = 7115979649947958701L;

    public NoActiveTableException() {
	super("No active table is chosen in current database");
    }
}
