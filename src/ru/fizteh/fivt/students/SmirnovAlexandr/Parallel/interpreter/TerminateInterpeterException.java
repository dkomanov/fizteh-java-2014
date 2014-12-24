package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter;

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
