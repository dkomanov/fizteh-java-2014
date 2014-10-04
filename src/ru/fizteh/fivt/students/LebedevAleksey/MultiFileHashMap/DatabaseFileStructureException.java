package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.LoadOrSaveError;

public class DatabaseFileStructureException extends LoadOrSaveError {
    public DatabaseFileStructureException(String message) {
        super(message);
    }

    public DatabaseFileStructureException(String message, Exception ex) {
        super(message, ex);
    }
}
