package ru.fizteh.fivt.students.Bulat_Galiev.proxy.InterpreterPackage;

public class ExitException extends RuntimeException {
    private static final long serialVersionUID = 3296621506567189545L;
    private final int status;

    public ExitException(final int exitStatus) {
        super();
        status = exitStatus;
    }

    public final int getStatus() {
        return status;
    }
}
