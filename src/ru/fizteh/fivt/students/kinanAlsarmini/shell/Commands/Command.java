package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import java.io.IOException;

public interface Command<State> {
    String getCommandName();

    String getHelpString();

    void executeCommand(String params, State shellState) throws IOException;
}
