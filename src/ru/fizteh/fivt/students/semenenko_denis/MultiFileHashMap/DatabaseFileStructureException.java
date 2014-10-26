package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

/**
 * Created by denny_000 on 23.10.2014.
 */
public class DatabaseFileStructureException extends WorkWithMemoryException {
    public DatabaseFileStructureException(String message) {
        super(message);
    }

    public DatabaseFileStructureException(String message, Exception e) {
        super(message, e);
    }
}
