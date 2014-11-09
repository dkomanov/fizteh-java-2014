package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;

import java.io.IOException;
import java.nio.file.Path;
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
    private TableImpl activeTable;

    /**
     * Establishes a database instance on given folder.<br/> If the folder exists, the old database
     * is used.<br/> If the folder does not exist, a new database is created within the folder.
     * @param dbDirectory
     * @throws DatabaseException
     */
    private Database(Path dbDirectory) {
        this.dbDirectory = dbDirectory;
        DBTableProviderFactory factory = new DBTableProviderFactory();
        this.provider = factory.create(dbDirectory.toString());
    }

    public static Database establishDatabase(Path dbDirectory) {
        return new Database(dbDirectory);
    }

    private void checkCurrentTableIsOpen() throws NoActiveTableException {
        if (activeTable == null) {
            throw new NoActiveTableException();
        }
    }

    /**
     * Creates a new empty table with specified name
     * @param tableName
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException
     *         I/O errors and name duplication errors are here
     */
    public boolean createTable(String tableName) throws IllegalArgumentException {
        return provider.createTable(tableName) != null;
    }

    /**
     * Deletes given table from file system.
     * @param tableName
     *         name of table to drop
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException
     *         if tablename does not exist or failed to delete
     */
    public void dropTable(String tableName) throws IllegalArgumentException {
        provider.removeTable(tableName);

        if (activeTable != null && activeTable.getName().equals(tableName)) {
            activeTable = null;
        }
    }

    public TableImpl getActiveTable() throws NoActiveTableException {
        checkCurrentTableIsOpen();
        return activeTable;
    }

    public Path getDbDirectory() {
        return dbDirectory;
    }

    /**
     * Writes all changes in the database to file system.
     * @throws IOException
     */
    public int commit() {
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
        Set<Entry<String, TableImpl>> tables = provider.listTables();
        for (Entry<String, TableImpl> table : tables) {
            if (table.getValue() == null) {
                System.out.println(table.getKey() + " corrupt");
            } else {
                System.out.println(table.getKey() + ' ' + table.getValue().size());
            }
        }
    }

    /**
     * Saves all changes to the current table (if not null) and prepares table with the given name
     * for use.
     * @param tableName
     *         name of table to use.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException
     *         if failed to save changes for current table or failed to load new table.
     */
    public void useTable(String tableName) throws DatabaseException, IllegalArgumentException {
        if (activeTable != null) {
            if (tableName.equals(activeTable.getName())) {
                return;
            }

            int uncommitted = activeTable.getUncommittedChangesCount();
            if (uncommitted != 0) {
                throw new DatabaseException(String.format("%d unsaved changes", uncommitted));
            }
        }

        TableImpl oldActiveTable = activeTable;

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
