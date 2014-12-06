package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.IOException;

public interface Command {
    String getName();
    void executeCmd(Shell shell, String[] args) throws IOException;
}
