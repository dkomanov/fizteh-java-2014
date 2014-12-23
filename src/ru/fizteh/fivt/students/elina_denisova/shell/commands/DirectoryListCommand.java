package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import ru.fizteh.fivt.students.elina_denisova.shell.Shell;

import java.util.ArrayList;


public class DirectoryListCommand implements Command {
    private Shell link;

    public DirectoryListCommand(final Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) {
        if (!arguments.isEmpty()) {
            throw new IllegalArgumentException("too many arguments");
        }
        for (String fileName : link.getWorkDirectory().list()) {
            System.out.println(fileName);
        }
    }

    @Override
    public String getName() {
        return "ls";
    }
}
