package ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions;

public class IllegalNumberOfArgumentsException extends IllegalArgumentException {

    /**
     *
     * @param commandName Command name.
     */
    public IllegalNumberOfArgumentsException(final String commandName) {
        super("illegal number of arguments in \"" + commandName + "\"");
    }
}
