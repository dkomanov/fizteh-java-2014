package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParserException;

public class ArgumentException extends ParserException {
    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable ex) {
        super(message, ex);
    }
}
