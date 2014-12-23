package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

public class ExitException extends Exception {
    private final int status;

    public final int getStatus() {
        return status;
    }

    public ExitException(final String message, final int exitStatus) {
        super(message);
        status = exitStatus;
    }

    public ExitException(final int exitStatus) {
        super("");
        status = exitStatus;
    }
}
