package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands.BaseFileMapShellState;

import java.io.IOException;
import java.util.AbstractMap;

public interface BaseDatabaseShellState<Table, Key, Value> extends BaseFileMapShellState<Table, Key, Value> {
    Table useTable(String name);

    void dropTable(String name) throws IOException;

    Table createTable(String parameters);

    String getActiveTableName();

    AbstractMap<String, Integer> getTables();
}
