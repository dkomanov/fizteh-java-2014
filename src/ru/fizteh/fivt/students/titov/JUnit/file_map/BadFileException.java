package ru.fizteh.fivt.students.titov.JUnit.file_map;

import java.io.IOException;

public class BadFileException extends IOException {
    public BadFileException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        return cause.getClass().getName() + ": " + cause.getMessage();
    }
}
