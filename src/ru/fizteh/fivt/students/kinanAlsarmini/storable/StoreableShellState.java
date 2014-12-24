package ru.fizteh.fivt.students.kinanAlsarmini.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands.BaseDatabaseShellState;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.TableInfo;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.List;

public class StoreableShellState implements BaseDatabaseShellState<Table, String, Storeable> {
    Table table;
    public DatabaseTableProvider provider;

    @Override
    public AbstractMap<String, Integer> getTables() {
        return provider.getTables();
    }

    @Override
    public List<String> listKeys() {
        return table.list();
    }

    @Override
    public Table useTable(String name) {
        table = provider.getTable(name);

        return table;
    }

    @Override
    public void dropTable(String name) throws IOException {
        provider.removeTable(name);
    }

    @Override
    public Table createTable(String parameters) {
        TableInfo info = null;

        info = StoreableUtils.parseCreateCommand(parameters);

        try {
            return provider.createTable(info.getTableName(), info.getColumnTypes());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getActiveTableName() {
        return table.getName();
    }

    @Override
    public Storeable put(String key, Storeable value) {
        return table.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return table.remove(key);
    }

    @Override
    public Storeable get(String key) {
        return table.get(key);
    }

    @Override
    public int commit() {
        try {
            return table.commit();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String keyToString(String key) {
        return key;
    }

    @Override
    public String valueToString(Storeable storeable) {
        String st = provider.serialize(table, storeable);

        return st;
    }

    @Override
    public String parseKey(String key) {
        return key;
    }

    @Override
    public Storeable parseValue(String value) {
        try {
            return provider.deserialize(table, value);
        } catch (ParseException e) {
            return null;
        }
    }
}
