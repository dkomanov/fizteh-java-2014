package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;


public final class CpCommand implements Command {
    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(3, args.length, 4)) {
            ErrorHandler.countArguments("cp");
        }
        boolean recursive = false;
        if (args.length == 4) {
            if (!args[1].equals("-r")) {
                ErrorHandler.invalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File src = new File(env.currentDirectory +
                File.separator + args[args.length - 2]);
        File dst = new File(env.currentDirectory +
                File.separator + args[args.length - 1]);
        if (!src.exists()) {
            ErrorHandler.noFile("cp", src.getName());
        }
        if (!src.isDirectory() && recursive) {
            ErrorHandler.isDirectory("cp", src.getName());
        }
        Executor.copy(src, dst);
    }
}
