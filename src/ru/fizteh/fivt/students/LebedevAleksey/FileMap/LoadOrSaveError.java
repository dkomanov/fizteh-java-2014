package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

public class LoadOrSaveError extends DatabaseException {
    public LoadOrSaveError(String message) {
        super(message);
    }

    public LoadOrSaveError(String message, Exception ex) {
        super(message, ex);
    }
}
