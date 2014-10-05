package ru.fizteh.fivt.students.SergeyAksenov.shell;

public class PwdCommand implements Command {

    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            ErrorHandler.countArguments("pwd");
        }
        System.out.println(env.currentDirectory);
    }
}
