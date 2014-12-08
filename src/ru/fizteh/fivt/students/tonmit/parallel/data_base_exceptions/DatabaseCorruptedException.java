package ru.fizteh.fivt.students.tonmit.parallel.data_base_exceptions;

public class DatabaseCorruptedException extends Exception {
    public DatabaseCorruptedException(String message) {
        super(message);
    }
}
