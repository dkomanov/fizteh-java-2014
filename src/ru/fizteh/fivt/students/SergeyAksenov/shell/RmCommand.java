package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;

public final class RmCommand implements Command {
    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(2, args.length, 3)) {
            ErrorHandler.countArguments("rm");
        }
        boolean recursive = false;
        if (args.length == 3) {
            if (!args[1].equals("-r")) {
                ErrorHandler.invalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File fileToRem = new File(env.currentDirectory +
                File.separator + args[args.length - 1]);
        if (!fileToRem.exists()) {
            ErrorHandler.noFile("rm", args[args.length - 1]);
        }
        if (!recursive && fileToRem.isDirectory()) {
            ErrorHandler.isDirectory("rm", args[args.length - 1]);
        }
        Executor.delete(fileToRem);
    }
}
