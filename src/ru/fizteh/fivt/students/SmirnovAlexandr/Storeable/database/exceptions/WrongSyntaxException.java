package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.exceptions;

public class WrongSyntaxException extends IllegalArgumentException {
    public WrongSyntaxException(final String msg) {
        super(msg + ": wrong syntax");
    }
}
