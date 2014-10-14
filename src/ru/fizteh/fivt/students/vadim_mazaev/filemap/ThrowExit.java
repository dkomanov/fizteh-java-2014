package ru.fizteh.fivt.students.vadim_mazaev.filemap;

public class ThrowExit extends Throwable {
    private static final long serialVersionUID = 1L;

    public ThrowExit(final boolean isExitStatusOk) {
        exitStatus = isExitStatusOk;
    }
    public final boolean getExitStatus() {
        return exitStatus;
    }
    private boolean exitStatus;
}
