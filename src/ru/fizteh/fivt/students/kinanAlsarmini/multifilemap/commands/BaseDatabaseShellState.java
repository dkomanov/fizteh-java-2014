package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands.BaseFileMapShellState;

import java.io.IOException;
import java.util.AbstractMap;

public interface BaseDatabaseShellState<Table, Key, Value> extends BaseFileMapShellState<Table, Key, Value> {
    public Table useTable(String name);

    public void dropTable(String name) throws IOException;

    public Table createTable(String parameters);

    public String getActiveTableName();

    public AbstractMap<String, Integer> getTables();
}
