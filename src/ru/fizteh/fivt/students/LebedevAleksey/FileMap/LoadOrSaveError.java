package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

public class LoadOrSaveError extends DatabaseException {
    LoadOrSaveError(String message) {
        super(message);
    }

    LoadOrSaveError(String message, Exception ex) {
        super(message, ex);
    }
}
