package ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions;

public class WrongSyntaxException extends IllegalArgumentException {
    public WrongSyntaxException(final String msg) {
        super(msg + ": wrong syntax");
    }
}
