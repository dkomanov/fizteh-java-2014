package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import java.io.IOException;

public abstract class AbstractCommand<State> implements Command<State> {
    private final String commandName;
    private final String helpString;

    public AbstractCommand() {
        this("", "");
    }

    public AbstractCommand(String commandName, String helpString) {
        this.commandName = commandName;
        this.helpString = helpString;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getHelpString() {
        return helpString;
    }
}
