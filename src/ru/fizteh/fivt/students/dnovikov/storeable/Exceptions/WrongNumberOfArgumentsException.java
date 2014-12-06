package ru.fizteh.fivt.students.dnovikov.storeable.Exceptions;

public class WrongNumberOfArgumentsException extends IllegalArgumentException {
    public WrongNumberOfArgumentsException(String cmdName) {
        super(cmdName + ": wrong number of arguments");
    }


}
