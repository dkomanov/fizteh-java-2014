package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter.exceptions;

public class WrongCommandException extends IllegalArgumentException {
    public WrongCommandException() {
        super();
    }

    public WrongCommandException(final String commandName) {
        super("wrong command: " + commandName);
    }
}
