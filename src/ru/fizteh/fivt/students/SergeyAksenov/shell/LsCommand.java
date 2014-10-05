package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;

public final class LsCommand implements Command {

    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            ErrorHandler.countArguments("ls");
        }
        File directory = new File(env.currentDirectory);
        String[] fileNames = directory.list();
        for (String name : fileNames) {
            System.out.println(name);
        }
    }
}
