package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpCommand<State> extends AbstractCommand<State> {
    public HelpCommand() {
        super("help", "help");
    }

    public HelpCommand(List<Command<?>> commands) {
        super("help", "help");

        for (final Command<?> command : commands) {
            this.commands.put(command.getCommandName(), command);
        }
    }

    public void executeCommand(String params, State shellState) throws IOException {
        if (params.length() > 0) {
            if (params.length() > 1) {
                throw new IllegalArgumentException("too many arguments");
            }

            printInfo(params);
        } else {
            for (final String commandName : commands.keySet()) {
                printInfo(commandName);
            }
        }
    }

    private void printInfo(String commandName) {
        if (!commands.containsKey(commandName)) {
            throw new IllegalArgumentException(String.format("'%s': command not found", commandName));
        }

        Command command = commands.get(commandName);
        System.out.println(command.getHelpString());
    }

    private HashMap<String, Command> commands = new HashMap<String, Command>();
}
