package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

public class WrongSyntaxException extends IllegalArgumentException {
    public WrongSyntaxException() { }
    public WrongSyntaxException(final String msg) {
        super(msg + ": wrong syntax");
    }
}
