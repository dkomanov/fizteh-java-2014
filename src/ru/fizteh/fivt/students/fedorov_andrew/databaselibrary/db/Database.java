package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Database class responsible for a set of tables assigned to it.
 * @author phoenix
 */
public class Database implements AutoCloseable {
    private final TableProvider provider;
    /**
     * Root directory of all database files
     */
    private final Path dbDirectory;
    /**
     * Table in use.<br/> All operations (like {@code put}, {@code get}, etc.) are performed with
     * this table.
     */
    private Table activeTable;

    /**
     * Establishes a database instance on given folder.<br/> If the folder exists, the old database
     * is used.<br/> If the folder does not exist, a new database is created within the folder.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     */
    public Database(Path dbDirectory) throws DatabaseIOException {
        this.dbDirectory = dbDirectory;
        DBTableProviderFactory factory = new DBTableProviderFactory();
        this.provider = factory.create(dbDirectory.toString());
    }

    public TableProvider getProvider() {
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
            throws IllegalArgumentException, IOException {
        return provider.createTable(tableName, columnTypes) != null;
    }

    /**
     * Deletes given table from file system.
     * @param tableName
     *         Name of table to drop.
     */
    public void dropTable(String tableName) throws IllegalArgumentException, IOException {
        String activeTableName = activeTable == null ? null : activeTable.getName();

        provider.removeTable(tableName);

        if (tableName.equals(activeTableName)) {
            activeTable = null;
        }
    }

    @Override
    public void close() throws Exception {
        if (provider instanceof AutoCloseable) {
            ((AutoCloseable) provider).close();
        }
    }

    public Table getActiveTable() throws NoActiveTableException {
        checkCurrentTableIsOpen();
        return activeTable;
    }

    public Path getDbDirectory() {
        return dbDirectory;
    }

    /**
     * Writes all changes in the database to file system.
     */
    public int commit() throws IOException {
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
        List<String> tableNames = provider.getTableNames();

        for (String tableName : tableNames) {
            boolean valid;
            int changesCount = 0;

            try {
                Table table = provider.getTable(tableName);
                changesCount = table.size();
                valid = true;
            } catch (Exception exc) {
                valid = false;
            }

            System.out.println(String.format("%s %s", tableName, valid ? changesCount : "corrupt"));
        }
    }

    /**
     * Saves all changes to the current table (if not null) and prepares table with the given name for use.
     * @param tableName
     *         Name of table to use.
     */
    public void useTable(String tableName) throws IOException, IllegalArgumentException {
        if (activeTable != null) {
            if (tableName.equals(activeTable.getName())) {
                return;
            }

            int uncommitted = activeTable.getNumberOfUncommittedChanges();
            if (uncommitted != 0) {
                throw new DatabaseIOException(String.format("%d unsaved changes", uncommitted));
            }
        }

        Table oldActiveTable = activeTable;

        try {
            activeTable = provider.getTable(tableName);
            if (activeTable == null) {
                throw new IllegalArgumentException(tableName + " not exists");
            }
        } catch (Exception exc) {
            activeTable = oldActiveTable;
            throw exc;
        }
    }
}
