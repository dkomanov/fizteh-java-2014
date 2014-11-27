package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import java.io.IOException;

public class DatabaseIOException extends IOException {
    public DatabaseIOException(String message) {
        super(message);
    }

    public DatabaseIOException(String message, IOException cause) {
        super(message, cause);
    }

    public DatabaseIOException(IOException cause) {
        super(cause);
    }
}
