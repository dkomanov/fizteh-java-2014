package ru.fizteh.fivt.students.ekaterina_pogodina.JUnit;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableProvider;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;


public class DBaseTableProvider implements TableProvider {

    public TableManager tableManager;

    public DBaseTableProvider(String dir) throws Exception {
        tableManager = new TableManager(dir);
    }

    @Override
    public Table getTable(String name) {
        if (tableManager.basicTables.containsKey(name)) {
            try {
                return tableManager.basicTables.get(name);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Illegal key's name", e);
            }
        }
        return null;
    }

    @Override
    public Table createTable(String name) throws Exception {
        if (tableManager.basicTables.containsKey(name)) {
            return null;
        }
        tableManager.create(name);
        try {
            return tableManager.basicTables.get(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Illegal key's name", e);
        }
    }

    @Override
    public void removeTable(String name) throws Exception {
        if (!tableManager.basicTables.containsKey(name)) {
            throw new IllegalArgumentException("there is no such table");
        }
        try {
            boolean f = tableManager.drop(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Illegal table's name", e);
        }
    }
}
