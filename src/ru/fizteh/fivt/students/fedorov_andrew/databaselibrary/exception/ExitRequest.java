package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

/**
 * Created by phoenix on 28.10.14.
 */
public class ExitRequest extends RuntimeException {
    private int code;

    public ExitRequest(int code) {
        super("Exit request with status = " + code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
