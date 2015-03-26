package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.util.Map;
import java.util.Set;


public class DBProvider implements TableProvider {

    private TableManager tableManager;

    public TableManager getTableManager() {
        return tableManager;
    }

    private String illegalKeyName = "Illegal name of key";
    private String illegalTableName = "Illegal name of table";

    public DBProvider(String dir) {
        tableManager = new TableManager(dir);
    }

    @Override
    public Table getTable(String name) {
        if (tableManager.basicTables.containsKey(name)) {
            try {
                return tableManager.basicTables.get(name);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(illegalKeyName, e);
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
            throw new IllegalArgumentException(illegalKeyName, e);
        }
    }

   @Override
    public void removeTable(final String name) {
        if (!tableManager.basicTables.containsKey(name)) {
            throw new IllegalArgumentException(illegalTableName);
        }
        try {
            boolean f = tableManager.drop(name);
        } catch (Exception e) {
            throw new IllegalArgumentException(illegalTableName, e);
        }
    }

    public Set<Map.Entry<String, DBaseTable>> entrySet() {
        return getTableManager().tables.entrySet();
    }

}
