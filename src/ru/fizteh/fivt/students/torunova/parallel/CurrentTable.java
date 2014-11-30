package ru.fizteh.fivt.students.torunova.parallel;

/**
 * Created by nastya on 01.12.14.
 */
public class CurrentTable {
    private final ThreadLocal<TableWrapper> currentTable = new ThreadLocal<>();
    private DatabaseWrapper db;
    public CurrentTable(DatabaseWrapper db) {
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
        if (currentTable.get() == null) {
            return false;
        }
        return true;
    }
    public void reset() {
        currentTable.set(null);
    }
}
