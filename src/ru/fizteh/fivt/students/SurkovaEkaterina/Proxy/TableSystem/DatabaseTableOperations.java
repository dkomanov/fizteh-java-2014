package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.StoreableUsage;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class DatabaseTableOperations implements DatabaseShellOperationsInterface<Table, String, Storeable> {

    Table table;
    TableProvider provider;

    public DatabaseTableOperations(TableProvider provider) {
        this.provider = provider;
    }

    @Override
    public ThreadSafeDatabaseTable useTable(String name) {
        ThreadSafeDatabaseTable tempTable = (ThreadSafeDatabaseTable) provider.getTable(name);
        if (tempTable != null) {
            table = tempTable;
        }
        return tempTable;
    }

    @Override
    public void dropTable(String name) throws IOException {
        provider.removeTable(name);
        table = null;
    }

    @Override
    public Table createTable(String parameters) {
        TableInfo info;
        info = StoreableUsage.parseCreateCommand(parameters);
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
        if (table != null) {
            return table.rollback();
        } else {
            return 0;
        }
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
    public String valueToString(Storeable value) {
        String string = provider.serialize(table, value);
        return string;
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
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public int exit() {
        try {
            ((ThreadSafeDatabaseTableProvider) provider).exit();
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }

    @Override
    public void showTables() {
        ((ThreadSafeDatabaseTableProvider) provider).showTables();
    }

    @Override
    public List<String> list() {
        return table.list();
    }
}
