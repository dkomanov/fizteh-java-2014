package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowCommand<State extends BaseDatabaseShellState> extends AbstractCommand<State> {
    public ShowCommand() {
        super("show", "show tables");
    }

    public void executeCommand(String params, State shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        if (!parameters.get(0).equals("tables")) {
            throw new IllegalArgumentException("invalid arguments");
        }

        HashMap<String, Integer> tables = new HashMap<String, Integer>(shellState.getTables());

        for (Map.Entry<String, Integer> table : tables.entrySet()) {
            System.out.println(table.getKey() + " " + table.getValue());
        }
    }
}
