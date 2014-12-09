package ru.fizteh.fivt.students.SergeyAksenov.shell;


public interface Command {
    void run(final String[] args, final Environment env)
            throws ShellException, ShellExitException;
}
