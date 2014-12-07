package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

/**
 * Created by denny_000 on 02.11.2014.
 */

public class LoadOrSaveException extends RuntimeException {
    public LoadOrSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadOrSaveException(String message) {
        super(message);
    }
}
