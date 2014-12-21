package ru.fizteh.fivt.students.LebedevAleksey.junit;

/**
 * Created by Алексей on 01.11.2014.
 * This is wrapper of exceptions, made for implementation of interfaces
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        return cause.getClass().getName() + ": " + cause.getMessage();
    }
}
