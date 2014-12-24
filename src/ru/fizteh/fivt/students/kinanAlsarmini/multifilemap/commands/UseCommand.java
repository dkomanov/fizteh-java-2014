package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.util.ArrayList;

public class UseCommand<Table, Key, Value, State extends BaseDatabaseShellState<Table, Key, Value>>
                                                    extends AbstractCommand<State> {
    public UseCommand() {
        super("use", "use <table name>");
    }

    public void executeCommand(String params, State shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        Table newTable = null;

        try {
            newTable = shellState.useTable(parameters.get(0));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s not exists", parameters.get(0)));
            return;
        }

        System.out.println(String.format("using %s", shellState.getActiveTableName()));
    }
}
