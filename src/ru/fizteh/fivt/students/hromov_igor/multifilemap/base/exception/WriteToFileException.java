package ru.fizteh.fivt.students.hromov_igor.multifilemap.base.exception;

public class WriteToFileException extends IllegalArgumentException {
    public WriteToFileException() {
        super("Can't write to file");
    }
}
