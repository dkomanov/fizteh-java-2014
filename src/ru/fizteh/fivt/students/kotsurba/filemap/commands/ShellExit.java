package ru.fizteh.fivt.students.kotsurba.filemap.commands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public final class ShellExit extends SimpleShellCommand {

    public ShellExit() {
        setName("exit");
        setNumberOfArgs(1);
        setHint("usage: exit");
    }

    @Override
    public void run() {
        System.exit(0);
    }

}
