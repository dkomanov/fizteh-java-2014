package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

public class IllegalCommandException extends IllegalArgumentException {
    private final String errMessage;
    public String getErrMessage() {
        return errMessage;
    }
    public IllegalCommandException(String message) {
        errMessage = message;
    }
    public IllegalCommandException() {
        errMessage = "";
    }
}
