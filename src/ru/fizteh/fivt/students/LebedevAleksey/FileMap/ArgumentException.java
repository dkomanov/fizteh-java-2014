package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

public class ArgumentException extends DatabaseException {
    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable ex) {
        super(message, ex);
    }
}
