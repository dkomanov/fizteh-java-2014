package ru.fizteh.fivt.students.dnovikov.filemap;

public class WrongNumberOfArgumentsException extends IllegalArgumentException {
    private String cmd;

    WrongNumberOfArgumentsException(String cmdName) {
        cmd = new String(cmdName);
    }

    public String getMessage() {
        return new String(cmd + ": wrong number of arguments");
    }
}
