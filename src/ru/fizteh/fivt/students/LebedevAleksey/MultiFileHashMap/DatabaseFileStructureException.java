package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class DatabaseFileStructureException extends Exception {
    public DatabaseFileStructureException(String message) {
        super(message);
    }

    public DatabaseFileStructureException(String message, Exception ex) {
        super(message, ex);
    }
}
