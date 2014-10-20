package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions;

public class IsNotADirectoryException extends IllegalArgumentException {
    public IsNotADirectoryException() { }
    public IsNotADirectoryException(final String msg) {
        super(msg + " is not a directory");
    }
}
