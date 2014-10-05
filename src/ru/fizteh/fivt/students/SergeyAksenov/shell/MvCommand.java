package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;

public final class MvCommand implements Command {
    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(3, args.length, 3)) {
            ErrorHandler.countArguments("mv");
        }
        File src = new File(env.currentDirectory + File.separator + args[1]);
        File dst = new File(env.currentDirectory + File.separator + args[2]);
        if (!src.exists()) {
            ErrorHandler.noFile("mv", src.getName());
        }
        if (src.equals(dst)) {
            return;
        }
        try {
            Executor.copy(src, dst);
        } catch (ShellException e) {
            String msg = e.getMessage();
            msg = msg.replaceFirst("rm", "mv");
            throw new ShellException(msg);
        }
        try {
            Executor.delete(src);
        } catch (ShellException e) {
            String msg = e.getMessage();
            msg = msg.replaceFirst("rm", "mv");
            throw new ShellException(msg);
        }
    }
}
