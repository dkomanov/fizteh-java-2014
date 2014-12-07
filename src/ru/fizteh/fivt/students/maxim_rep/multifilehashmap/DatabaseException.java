package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

@SuppressWarnings("serial")
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super("Database error: " + message);
    }

    public DatabaseException(String message, Exception ex) {
        super("Database error: " + message, ex);
    }
}
