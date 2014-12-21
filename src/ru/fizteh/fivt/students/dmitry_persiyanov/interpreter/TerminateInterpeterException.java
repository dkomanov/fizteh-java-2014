package ru.fizteh.fivt.students.dmitry_persiyanov.interpreter;

/**
 * Created by drack3800 on 08.11.2014.
 */
public class TerminateInterpeterException extends RuntimeException {
    private final int exitStatus;

    public TerminateInterpeterException(int exitStatus) {
        super();
        this.exitStatus = exitStatus;
    }

    public int getExitStatus() {
        return exitStatus;
    }
}
