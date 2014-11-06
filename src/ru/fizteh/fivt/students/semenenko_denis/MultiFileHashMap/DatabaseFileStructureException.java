package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

/**
 * Created by denny_000 on 02.11.2014.
 */

public class DatabaseFileStructureException extends RuntimeException {
    public DatabaseFileStructureException(String message) {
        super(message);
    }

    public DatabaseFileStructureException(String message, Exception ex) {
        super(message, ex);
    }
}