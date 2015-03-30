package ru.fizteh.fivt.students.hromov_igor.multifilemap.base.exception;

public class DirCreateException extends IllegalArgumentException {

    public DirCreateException(Exception e) {
        super("Can't create directory", e);
    }
}

