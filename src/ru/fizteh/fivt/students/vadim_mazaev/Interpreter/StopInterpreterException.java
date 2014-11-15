package ru.fizteh.fivt.students.vadim_mazaev.Interpreter;

public final class StopInterpreterException extends RuntimeException {
    private static final long serialVersionUID = -1498754023926487549L;
    private final int exitStatus;
    
    public StopInterpreterException(int exitStatus) {
        super();
        this.exitStatus = exitStatus;
    }
    
    public int getExitStatus() {
        return exitStatus;
    }
}
