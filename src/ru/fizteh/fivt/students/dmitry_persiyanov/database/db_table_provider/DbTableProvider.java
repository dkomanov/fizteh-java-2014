package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.SyntaxCheckers;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.WrongTableNameException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DbTableProvider implements TableProvider {
    public DbTableProvider(final File rootDir) {
        if (rootDir == null) {
            throw new NullPointerException();
        } else if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new RuntimeException("can't create directory: " + rootDir.getAbsolutePath());
            }
        } else if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException(rootDir.getAbsolutePath() + " isn't a directory");
        }
        this.rootDir = rootDir;
        try {
            loadTables();
        } catch (IOException e) {
            throw new RuntimeException("can't load table names from: " + rootDir.getPath()
                    + ", [" + e.getMessage() + "]");
        }
    }
    private File rootDir;
    private DbTable currentTable;
    private Map<String, DbTable> tables = new HashMap<>();

    public DbTable getCurrentTable() {
        return currentTable;
    }

    public void dump() throws IOException {
        if (currentTable != null) {
            currentTable.commit();
        }
    }

    public boolean containsTable(final String tableName) {
        return tables.containsKey(tableName);
    }

    @Override
    public DbTable getTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!tables.containsKey(tableName)) {
            return null;
        } else {
            DbTable table = tables.get(tableName);
            if (table == null) {
                tables.put(tableName, new DbTable(getTablePath(tableName).toFile()));
            }
            return tables.get(tableName);
        }
    }

    @Override
    public DbTable createTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else {
            try {
                if (!containsTable(tableName)) {
                    Path tablePath = getTablePath(tableName);
                    Files.createDirectory(tablePath);
                    DbTable table = new DbTable(tablePath.toFile());
                    tables.put(tableName, table);
                    return table;
                } else {
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException("can't create table: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!containsTable(tableName)) {
            throw new IllegalStateException("there is no tables with name \"" + tableName + "\"");
        } else {
            if (currentTable != null && currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
            tables.remove(tableName);
            try {
                purgeTable(getTablePath(tableName));
            } catch (IOException e) {
                throw new RuntimeException("can't remove table: " + e.getMessage());
            }
        }
    }

    private void purgeTable(final Path tablePath) throws IOException {
        File[] dirs = new File(tablePath.toString()).listFiles();
        for (File dir : dirs) {
            purgeDir(dir);
        }
        Files.delete(tablePath);
    }

    private void purgeDir(final File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            Files.delete(file.toPath());
        }
        Files.delete(dir.toPath());
    }

    /**
     * @param tableName
     * @return  0 if succeed, otherwise returns number of uncommitted changes in currentTable
     * If number of uncommitted changes isn't zero then table isn't changing.
     * @throws IOException if it is impossible to dump current table.
     */
    public int useTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        } else {
            if (currentTable == null) {
                currentTable = tables.get(tableName);
                return 0;
            } else {
                boolean currentAndNewTablesAreDistinct = !currentTable.getName().equals(tableName);
                if (currentAndNewTablesAreDistinct) {
                    if (currentTable.numOfUncommittedChanges() == 0) {
                        currentTable = tables.get(tableName);
                        return 0;
                    } else {
                        return currentTable.numOfUncommittedChanges();
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> res = new HashMap<>();
        for (String tableName : tables.keySet()) {
            if (tables.get(tableName) == null) {
                tables.put(tableName, new DbTable(getTablePath(tableName).toFile()));
            }
            res.put(tableName, tables.get(tableName).size());
        }
        return res;
    }

    private Path getTablePath(final String tableName) {
        return Paths.get(rootDir.getAbsolutePath(), tableName).normalize();
    }

    private void loadTables() throws IOException {
        String[] tableNames = rootDir.list();
        tables.clear();
        for (String name : tableNames) {
            tables.put(name, null);
        }
    }
}
