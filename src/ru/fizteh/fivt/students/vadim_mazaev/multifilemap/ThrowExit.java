package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

public class ThrowExit extends Throwable {
    private static final long serialVersionUID = -9062929441438943525L;
    private boolean exitSuccess;
    
    public ThrowExit(boolean exitSuccess, Exception cause) {
        super(cause);
        this.exitSuccess = exitSuccess;
    }
    public final boolean isExitSuccess() {
        return exitSuccess;
    }
}
