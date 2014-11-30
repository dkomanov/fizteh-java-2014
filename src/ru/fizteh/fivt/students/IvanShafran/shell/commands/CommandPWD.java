package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.Shell;

import java.util.ArrayList;

public class CommandPWD extends Command {
    public Shell shellLink;

    public CommandPWD(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        System.out.println(shellLink.getWorkingDirectory());
    }

}
