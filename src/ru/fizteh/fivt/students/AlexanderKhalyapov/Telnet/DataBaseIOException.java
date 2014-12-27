package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.IOException;

public class DataBaseIOException extends IOException {
    public DataBaseIOException(String message, Exception cause) {
        super(message, cause);
    }

    public DataBaseIOException(String message) {
        super(message);
    }
}
