package ru.fizteh.fivt.students.torunova.parallel.database;

/**
 * Created by nastya on 01.12.14.
 */
public class TableHolder {
    private final ThreadLocal<TableWrapper> currentTable = new ThreadLocal<>();
    private DatabaseWrapper db;
    public TableHolder(DatabaseWrapper db) {
        this.db = db;
        currentTable.set(null);
    }
    public TableWrapper get() {
        return currentTable.get();
    }
    public DatabaseWrapper getDb() {
        return db;
    }
    public boolean set(String name) {
        currentTable.set((TableWrapper) db.getTable(name));
        return currentTable != null;
    }
    public void reset() {
        currentTable.set(null);
    }
}
