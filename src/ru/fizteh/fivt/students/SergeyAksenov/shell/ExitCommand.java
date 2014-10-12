package ru.fizteh.fivt.students.SergeyAksenov.shell;


public final class ExitCommand implements Command {
    public void run(final String[] args, final Environment env)
            throws ShellException, ShellExitException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            ErrorHandler.countArguments("exit");
        }
        throw new ShellExitException();
    }
}
