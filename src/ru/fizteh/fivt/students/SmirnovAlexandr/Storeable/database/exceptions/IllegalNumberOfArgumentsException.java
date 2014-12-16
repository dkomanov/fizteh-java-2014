package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.exceptions;

public class IllegalNumberOfArgumentsException extends IllegalArgumentException {

    public IllegalNumberOfArgumentsException(final String commandName) {
        super("illegal number of arguments in \"" + commandName + "\"");
    }
}
