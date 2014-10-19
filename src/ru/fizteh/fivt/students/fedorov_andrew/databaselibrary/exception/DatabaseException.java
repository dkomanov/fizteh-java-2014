package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 5087034802841939526L;

    public DatabaseException (String message) {
	super (message);
    }
    
    public DatabaseException (String message, Throwable cause) {
	super (message, cause);
    }
    
    public DatabaseException (Throwable cause) {
	super (cause);
    }
}
