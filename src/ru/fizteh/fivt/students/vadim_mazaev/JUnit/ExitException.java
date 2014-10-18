package ru.fizteh.fivt.students.vadim_mazaev.JUnit;

public class ExitException extends Exception {
    private static final long serialVersionUID = 2146673936560423416L;
    private final int exitStatus;

    public ExitException(final int exitStatus) {
        this.exitStatus = exitStatus;
    }
    public final int getExitStatus() {
        return exitStatus;
    }
}
