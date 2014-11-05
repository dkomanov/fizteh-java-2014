package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

public class DatabaseIOException extends RuntimeException {
    private final String errMessage;
    public String getErrMessage() {
        return  errMessage;
    }
    public DatabaseIOException(String message) {
        errMessage = message;
    }
}
