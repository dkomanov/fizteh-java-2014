package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions;

public class IllegalNumberOfArgumentsException extends IllegalArgumentException {
    public IllegalNumberOfArgumentsException(final String commandName) {
        super("illegal number of arguments in \"" + commandName + "\"");
    }
}
