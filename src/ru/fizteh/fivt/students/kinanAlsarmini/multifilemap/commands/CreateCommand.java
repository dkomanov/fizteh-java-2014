package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.MultifileMapUtils;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;

import java.util.ArrayList;

public class CreateCommand<Table, Key, Value, State extends BaseDatabaseShellState<Table, Key, Value>>
                                                        extends AbstractCommand<State> {
    public CreateCommand() {
        super("create", "create <table name>");
    }

    public void executeCommand(String params, State shellState) {
        String tableName = MultifileMapUtils.parseTableName(params);

        Table newTable = null;
        try {
            newTable = shellState.createTable(params);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s exists", tableName));
        } else {
            System.out.println("created");
        }
    }
}
