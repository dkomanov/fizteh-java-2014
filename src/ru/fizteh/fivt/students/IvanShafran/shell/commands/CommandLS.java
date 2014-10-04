package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.Shell;

import java.util.ArrayList;

public class CommandLS extends Command {
    public Shell shellLink;

    public CommandLS(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        if (args.size() != 0) {
            throw new Exception("Usage: ls");
        }

        for (String name : shellLink.getWorkingDirectory().list()) {
            System.out.println(name);
        }
    }

}
