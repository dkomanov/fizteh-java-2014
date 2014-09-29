package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class DirectoryListCommand implements Command {
    public Shell link;

    public DirectoryListCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) {
        for (String fileName : link.getWorkDirectory().list()) {
            System.out.println(fileName);
        }
    }

    @Override
    public String getName() {
        return "ls";
    }
}
