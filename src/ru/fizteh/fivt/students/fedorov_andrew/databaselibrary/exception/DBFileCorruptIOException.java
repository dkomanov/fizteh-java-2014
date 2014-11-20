package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import java.io.IOException;

/**
 * This exception describes situation when database file is corrupt and cannot be properly read.
 * @author phoenix
 */
public class DBFileCorruptIOException extends DatabaseIOException {

    private static final long serialVersionUID = 2107102137382933269L;

    public DBFileCorruptIOException(String reason) {
        this(reason, null);
    }

    public DBFileCorruptIOException(String reason, IOException cause) {
        super("DB file is corrupt: " + reason, cause);
    }
}
