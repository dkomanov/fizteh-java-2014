package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
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
