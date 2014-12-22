package ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.exceptions;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class WrongCommandException extends IllegalArgumentException {
    public WrongCommandException() {
        super();
    }

    public WrongCommandException(final String commandName) {
        super("wrong command: " + commandName);
    }
}
