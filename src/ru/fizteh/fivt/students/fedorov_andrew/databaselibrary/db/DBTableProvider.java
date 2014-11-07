package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TableCorruptException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DBTableProvider implements TableProvider {
    private Path databaseRoot;

    /**
     * Mapping between table names and tables. Corrupt tables are null.
     */
    private Map<String, TableImpl> tables;

    private Map<String, TableCorruptException> corruptTables;

    /**
     * Constructs a database table provider.
     * @param databaseRoot
     * @throws DatabaseException
     *         If failed to scan database directory.
     */
    DBTableProvider(Path databaseRoot) throws DatabaseException {
        this.databaseRoot = databaseRoot;
        this.tables = new HashMap<String, TableImpl>();
        this.corruptTables = new HashMap<>();
        reloadTables();
    }

    @Override
    public TableImpl getTable(String name) throws IllegalArgumentException {
        Utility.checkTableNameIsCorrect(name);
        if (tables.containsKey(name)) {
            TableImpl table = tables.get(name);
            if (table == null) {
                DatabaseException corruptionReason = corruptTables.get(name);
                throw new IllegalArgumentException(corruptionReason.getMessage(), corruptionReason);
            }
            return table;
        } else {
            return null;
        }
    }

    @Override
    public TableImpl createTable(String name) throws IllegalArgumentException {
        Utility.checkTableNameIsCorrect(name);
        Path tablePath = databaseRoot.resolve(name);

        if (Files.exists(tablePath)) {
            return null;
        } else {
            try {
                Files.createDirectory(tablePath);
            } catch (IOException exc) {
                throw new IllegalArgumentException(
                        "Bad name given", new DatabaseException(
                        "Failed to create table directory", exc));
            }
        }

        TableImpl newTable = TableImpl.createTable(tablePath);
        tables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException {
        Utility.checkTableNameIsCorrect(name);
        Path tablePath = databaseRoot.resolve(name);

        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("Table " + name + " not exists");
        }

        tables.remove(name);
        corruptTables.remove(name);

        if (!Files.exists(tablePath)) {
            return;
        }

        try {
            Utility.rm(tablePath, "drop");
        } catch (IOException exc) {
            // Mark as corrupt.
            tables.put(name, null);
            TableCorruptException corruptionReason = new TableCorruptException(
                    name, "Failed to drop table: " + exc.toString(), exc);
            corruptTables.put(name, corruptionReason);
            throw new IllegalArgumentException(corruptionReason.getMessage(), corruptionReason);
        }
    }

    /**
     * Scans database directory and reads all tables from it.
     * @throws DatabaseException
     */
    private void reloadTables() throws DatabaseException {
        tables.clear();

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(databaseRoot)) {
            for (Path tablePath : dirStream) {
                String tableName = tablePath.getFileName().toString();

                try {
                    TableImpl table = TableImpl.getTable(tablePath);
                    tables.put(tableName, table);
                } catch (DatabaseException exc) {
                    // mark as corrupt
                    tables.put(tableName, null);
                    corruptTables.put(
                            tableName,
                            (exc instanceof TableCorruptException
                             ? (TableCorruptException) exc
                             : new TableCorruptException(tableName, exc.getMessage(), exc)));
                }
            }
        } catch (IOException exc) {
            throw new DatabaseException("Failed to scan database directory", exc);
        }
    }

    public Set<Entry<String, TableImpl>> listTables() {
        return tables.entrySet();
    }

}
