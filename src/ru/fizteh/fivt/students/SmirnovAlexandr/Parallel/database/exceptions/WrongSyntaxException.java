package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions;

public class WrongSyntaxException extends IllegalArgumentException {
    public WrongSyntaxException(final String msg) {
        super(msg + ": wrong syntax");
    }
}
