package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import ru.fizteh.fivt.students.elina_denisova.shell.Shell;

import java.util.ArrayList;


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
