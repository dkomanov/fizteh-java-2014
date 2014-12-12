package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ExitException extends Exception {
    public ExitException(int givenCode) {
        code = givenCode;
        message = "";
    }

    public ExitException(String givenMessage) {
        message = givenMessage;
        code = 1;
    }

    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
