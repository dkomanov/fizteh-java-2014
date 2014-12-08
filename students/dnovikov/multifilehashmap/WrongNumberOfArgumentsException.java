package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

public class WrongNumberOfArgumentsException extends IllegalArgumentException {
    WrongNumberOfArgumentsException(String cmdName) {
        super(cmdName + ": wrong number of arguments");
    }
}
