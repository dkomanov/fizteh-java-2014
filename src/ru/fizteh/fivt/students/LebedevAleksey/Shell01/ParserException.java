package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

public class ParserException extends Exception {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable ex) {
        super(message, ex);
    }
}