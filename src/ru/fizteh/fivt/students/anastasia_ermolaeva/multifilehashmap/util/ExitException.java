package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util;

public class ExitException extends Exception {
    private final int status;
    private final String errMessage;

    public final int getStatus() {
        return status;
    }

    public ExitException(final String message, final int exitStatus) {
        errMessage = message;
        status = exitStatus;
    }

    public ExitException(final int exitStatus) {
        errMessage = "Error";
        status = exitStatus;
    }
}
