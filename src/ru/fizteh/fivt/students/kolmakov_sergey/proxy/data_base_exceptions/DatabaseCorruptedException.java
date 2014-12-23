package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_exceptions;

public class DatabaseCorruptedException extends Exception {
    public DatabaseCorruptedException(String message) {
        super(message);
    }
}
