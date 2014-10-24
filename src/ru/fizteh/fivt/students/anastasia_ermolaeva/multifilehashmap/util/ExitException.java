package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util;

public class ExitException extends Exception {
    private final int status;
    public  final int getStatus() {
        return status;
    }
    public ExitException(final int exitStatus) {
        status = exitStatus;
    }
}
