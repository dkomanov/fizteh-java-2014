package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.IOException;

public class Pwd implements Command {
    public final String getName() {
        return "pwd";
    }
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length > 0) {
            throw new IOException("Incorrect number of arguments");
        }
        System.out.println(shell.getState().getPath().toString());
    }
}
