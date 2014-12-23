package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.interpreter.exceptions;

public class WrongCommandException extends IllegalArgumentException {
    public WrongCommandException() {
        super();
    }

    public WrongCommandException(final String commandName) {
        super("wrong command: " + commandName);
    }
}
