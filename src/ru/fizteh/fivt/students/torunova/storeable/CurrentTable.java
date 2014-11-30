package ru.fizteh.fivt.students.torunova.storeable;

/**
 * Created by nastya on 01.12.14.
 */
public class CurrentTable {
    private TableWrapper currentTable;
    private DatabaseWrapper db;
    public CurrentTable(DatabaseWrapper db) {
        this.db = db;
        currentTable = null;
    }
    public TableWrapper get() {
        return currentTable;
    }
    public DatabaseWrapper getDb() {
        return db;
    }
    public boolean set(String name) {
        currentTable = (TableWrapper) db.getTable(name);
        if (currentTable == null) {
            return false;
        }
        return true;
    }
    public void reset() {
        currentTable = null;
    }
}
