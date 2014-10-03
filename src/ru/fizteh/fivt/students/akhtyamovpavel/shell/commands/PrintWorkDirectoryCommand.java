package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class PrintWorkDirectoryCommand implements Command {
    public Shell link;

    public PrintWorkDirectoryCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        System.out.println(link.getWorkDirectory());
    }

    @Override
    public String getName() {
        return "pwd";
    }

}
