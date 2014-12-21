package ru.fizteh.fivt.students.dnovikov.junit.Exceptions;

public class WrongNumberOfArgumentsException extends IllegalArgumentException {
    public WrongNumberOfArgumentsException(String cmdName) {
        super(cmdName + ": wrong number of arguments");
    }


}
