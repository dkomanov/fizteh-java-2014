package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.io.IOException;
import java.util.ArrayList;

public class DropCommand<State extends BaseDatabaseShellState> extends AbstractCommand<State> {
    public DropCommand() {
        super("drop", "drop <table name>");
    }

    public void executeCommand(String params, State shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        try {
            shellState.dropTable(parameters.get(0));
            System.out.println("dropped");
        } catch (IOException | IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
