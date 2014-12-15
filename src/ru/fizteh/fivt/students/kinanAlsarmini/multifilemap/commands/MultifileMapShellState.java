package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands.*;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.*;

import java.util.List;
import java.util.AbstractMap;

public class MultifileMapShellState extends FileMapShellState implements BaseDatabaseShellState<Table, String, String> {
    public DatabaseTableProvider tableProvider;

    @Override
    public Table useTable(String name) {
        table = tableProvider.getTable(name);
        
        return table;
    }

    @Override
    public void dropTable(String name) {
        tableProvider.removeTable(name);
    }

    @Override
    public Table createTable(String parameters) {
        List<String> params = CommandParser.parseParams(parameters);

        return tableProvider.createTable(params.get(0));
    }

    @Override
    public String getActiveTableName() {
        return table.getName();
    }

    @Override
    public AbstractMap<String, Integer> getTables() {
        return tableProvider.getTables();
    }
}
