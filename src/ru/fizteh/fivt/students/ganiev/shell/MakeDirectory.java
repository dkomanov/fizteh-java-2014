package ru.fizteh.fivt.students.ganiev.shell;

import java.io.IOException;

public class MakeDirectory implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        if (!FileCommander.getFile(arguments[0], shell).mkdir()) {
            throw new IOException("Unable to make directory " + arguments[0]);
        }
    }

    public int getNumberOfArguments() {
        return 1;
    }
}