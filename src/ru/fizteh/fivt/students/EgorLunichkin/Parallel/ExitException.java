package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ExitException extends Exception {
    public ExitException(int givenCode) {
        code = givenCode;
        message = "";
        cause = null;
    }

    public ExitException(Throwable givenCause) {
        code = 1;
        cause = givenCause;
        message = cause.getMessage();
    }

    private final int code;
    private final String message;
    private final Throwable cause;

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
