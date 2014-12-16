package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import java.util.ArrayList;


public class ExitCommand implements Command {

    @Override
    public void executeCommand(ArrayList<String> arguments) {
        System.exit(0);
    }

    @Override
    public String getName() {
        return "exit";
    }
}
