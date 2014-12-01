package ru.fizteh.fivt.students.torunova.junit;

/**
 * Created by nastya on 30.11.14.
 */
public class CurrentTable {
    private TableImpl currentTable;
    private Database db;
    public CurrentTable(Database db) {
        this.db = db;
        currentTable = null;
    }
    public TableImpl get() {
        return currentTable;
    }
    public Database getDb() {
        return db;
    }
    public boolean set(String name) {
        currentTable = (TableImpl) db.getTable(name);
        if (currentTable == null) {
            return false;
        }
        return true;
    }
    public void reset() {
        currentTable = null;
    }

}
