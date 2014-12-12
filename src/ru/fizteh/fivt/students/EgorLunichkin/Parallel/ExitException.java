package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ExitException extends Exception {
    public ExitException(int _code) {
        code = _code;
    }

    public ExitException(String msg) {
        message = msg;
        code = 1;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
