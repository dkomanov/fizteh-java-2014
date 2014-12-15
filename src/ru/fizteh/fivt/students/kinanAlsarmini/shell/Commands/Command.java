package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import java.io.IOException;

public interface Command<State> {
    public String getCommandName();

    public String getHelpString();

    public void executeCommand(String params, State shellState) throws IOException;
}
