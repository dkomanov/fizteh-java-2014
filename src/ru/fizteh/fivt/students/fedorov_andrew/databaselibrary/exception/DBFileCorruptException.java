package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

/**
 * This exception describes situation when database file is corrupt and cannot
 * be properly read.
 * 
 * @author phoenix
 * 
 */
public class DBFileCorruptException extends DatabaseException {

    private static final long serialVersionUID = 2107102137382933269L;

    public DBFileCorruptException(String reason) {
	this(reason, null);
    }
    
    public DBFileCorruptException(Throwable cause) {
	this(null, cause);
    }

    public DBFileCorruptException(String reason, Throwable cause) {
	super("DB file is corrupt" + (reason == null ? null : (": " + reason)),
		cause);
    }
}
