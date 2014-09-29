package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

public class ParserException extends Exception {
    ParserException(String message) {
        super(message);
    }

    ParserException(String message, Throwable ex) {
        super(message, ex);
    }
}
