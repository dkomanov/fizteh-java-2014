package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

public class NoActiveTableException extends Exception {
    public NoActiveTableException() {
        super("no table");
    }
}
