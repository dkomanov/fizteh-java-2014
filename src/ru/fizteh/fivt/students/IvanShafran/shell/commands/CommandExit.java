package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.Shell;

import java.util.ArrayList;

public class CommandExit extends Command {
    public Shell shellLink;

    public CommandExit(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        System.exit(0);
    }

}
