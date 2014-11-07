package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

public class NoActiveTableException extends IllegalStateException {
    private String errMessage;
    public String getErrMessage() {
        return errMessage;
    }
    public NoActiveTableException(String message) {
        errMessage = message;
    }
    public NoActiveTableException() {}
}
