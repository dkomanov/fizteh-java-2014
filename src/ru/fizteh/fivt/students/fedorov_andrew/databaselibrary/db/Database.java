package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Database class responsible for a set of tables assigned to it.
 * @author phoenix
 */
public class Database {
    private final DBTableProvider provider;
    /**
     * Root directory of all database files
     */
    private final Path dbDirectory;
    /**
     * Table in use.<br/> All operations (like {@code put}, {@code get}, etc.) are performed with
     * this table.
     */
    private StoreableTableImpl activeTable;

    /**
     * Establishes a database instance on given folder.<br/> If the folder exists, the old database
     * is used.<br/> If the folder does not exist, a new database is created within the folder.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     */
    private Database(Path dbDirectory) throws DatabaseIOException {
        this.dbDirectory = dbDirectory;
        DBTableProviderFactory factory = new DBTableProviderFactory();
        this.provider = factory.create(dbDirectory.toString());
    }

    public static Database establishDatabase(Path dbDirectory) throws DatabaseIOException {
        return new Database(dbDirectory);
    }

    public DBTableProvider getProvider() {
        return provider;
    }

    private void checkCurrentTableIsOpen() throws NoActiveTableException {
        if (activeTable == null) {
            throw new NoActiveTableException();
        }
    }

    /**
     * Creates a new empty table with specified name.
     */
    public boolean createTable(String tableName, List<Class<?>> columnTypes)
            throws IllegalArgumentException, DatabaseIOException {
        return provider.createTable(tableName, columnTypes) != null;
    }

    /**
     * Deletes given table from file system.
     * @param tableName
     *         Name of table to drop.
     */
    public void dropTable(String tableName) throws IllegalArgumentException, DatabaseIOException {
        String activeTableName = activeTable == null ? null : activeTable.getName();

        provider.removeTable(tableName);

        if (tableName.equals(activeTableName)) {
            activeTable = null;
        }
    }

    public StoreableTableImpl getActiveTable() throws NoActiveTableException {
        checkCurrentTableIsOpen();
        return activeTable;
    }

    public Path getDbDirectory() {
        return dbDirectory;
    }

    /**
     * Writes all changes in the database to file system.
     */
    public int commit() throws DatabaseIOException {
        // actually we have to persist the active table.
        if (activeTable != null) {
            return activeTable.commit();
        } else {
            return 0;
        }
    }

    public int rollback() {
        if (activeTable != null) {
            return activeTable.rollback();
        } else {
            return 0;
        }
    }

    public void showTables() {
        System.out.println("table_name row_count");
        Set<Entry<String, StoreableTableImpl>> tables = provider.listTables();
        for (Entry<String, StoreableTableImpl> table : tables) {
            if (table.getValue() == null) {
                System.out.println(table.getKey() + " corrupt");
            } else {
                System.out.println(table.getKey() + ' ' + table.getValue().size());
            }
        }
    }

    /**
     * Saves all changes to the current table (if not null) and prepares table with the given name for use.
     * @param tableName
     *         Name of table to use.
     */
    public void useTable(String tableName) throws DatabaseIOException, IllegalArgumentException {
        if (activeTable != null) {
            if (tableName.equals(activeTable.getName())) {
                return;
            }

            int uncommitted = activeTable.getUncommittedChangesCount();
            if (uncommitted != 0) {
                throw new DatabaseIOException(String.format("%d unsaved changes", uncommitted));
            }
        }

        StoreableTableImpl oldActiveTable = activeTable;

        try {
            activeTable = provider.getTable(tableName);
            if (activeTable == null) {
                throw new IllegalArgumentException(tableName + " not exists");
            }
        } catch (Throwable thr) {
            activeTable = oldActiveTable;
            throw thr;
        }
    }
}
