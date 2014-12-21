package ru.fizteh.fivt.students.torunova.storeable.database;

/**
 * Created by nastya on 01.12.14.
 */
public class TableHolder {
    private TableWrapper currentTable;
    private DatabaseWrapper db;
    public TableHolder(DatabaseWrapper db) {
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
        return currentTable != null;
    }
    public void reset() {
        currentTable = null;
    }
}
