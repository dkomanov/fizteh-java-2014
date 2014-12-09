package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParserException;

public class DatabaseException extends ParserException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable ex) {
        super(message, ex);
    }
}
