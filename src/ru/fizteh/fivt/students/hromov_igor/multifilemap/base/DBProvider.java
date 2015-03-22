package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;


public class DBProvider implements TableProvider {

    public TableManager tableManager;

    public DBProvider(String dir) throws Exception {
        tableManager = new TableManager(dir);
    }

    @Override
    public Table getTable(String name) {
        if (tableManager.basicTables.containsKey(name)) {
            try {
                return tableManager.basicTables.get(name);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Illegal name of key", e);
            }
        }
        return null;
    }

    @Override
    public Table createTable(final String name) {
        if (tableManager.basicTables.containsKey(name)) {
            return null;
        }
        try {
            tableManager.create(name);
            return tableManager.basicTables.get(name);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal name of key", e);
        }
    }

    @Override
    public void removeTable(final String name) {
        if (!tableManager.basicTables.containsKey(name)) {
            throw new IllegalArgumentException("There is no such table");
        }
        try {
            boolean f = tableManager.drop(name);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal name of table", e);
        }
    }
}
