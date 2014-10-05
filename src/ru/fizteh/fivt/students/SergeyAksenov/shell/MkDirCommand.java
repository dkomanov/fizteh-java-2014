package ru.fizteh.fivt.students.SergeyAksenov.shell;


import java.io.File;


public final class MkDirCommand implements Command {

    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            ErrorHandler.countArguments("mkdir");
        }
        File directory = new File(env.currentDirectory +
                File.separator + args[1]);
        if (directory.exists()) {
            ErrorHandler.fileExist("mkdir", args[1]);
        }
        if (!directory.mkdir()) {
            ErrorHandler.canNotCreate("mkdir");
        }
    }
}
