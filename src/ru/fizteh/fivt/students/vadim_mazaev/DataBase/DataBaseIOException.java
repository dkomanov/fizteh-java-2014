package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;

public class DataBaseIOException extends IOException {
    public DataBaseIOException(String message, Exception cause) {
        super(message, cause);
    }

    public DataBaseIOException(String message) {
        super(message);
    }
}
