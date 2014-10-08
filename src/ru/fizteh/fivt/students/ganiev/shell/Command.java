package ru.fizteh.fivt.students.ganiev.shell;

import java.io.IOException;

public interface Command {

    void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException, BreakingException;

    int getNumberOfArguments();
}
