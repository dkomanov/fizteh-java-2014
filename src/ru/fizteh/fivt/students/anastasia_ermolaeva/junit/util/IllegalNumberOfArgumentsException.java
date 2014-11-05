package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

public class IllegalNumberOfArgumentsException extends Exception {
    private final String errMessage;
    public String getErrMessage() {
        return errMessage;
    }
    public IllegalNumberOfArgumentsException(String message) {
        errMessage = message;
    }
}
