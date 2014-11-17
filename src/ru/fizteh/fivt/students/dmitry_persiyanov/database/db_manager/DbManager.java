package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.utils.SyntaxCheckers;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.WrongTableNameException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.TableManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DbManager implements TableProvider {
    public DbManager(final File rootDir) {
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
    private TableManager currentTable;
    private Map<String, TableManager> tables = new HashMap<>();

    public TableManager getCurrentTable() {
        return currentTable;
    }

    public void dumpCurrentTable() throws IOException {
        if (currentTable != null) {
            currentTable.dump();
        }
    }

    public boolean containsTable(final String tableName) {
        return tables.containsKey(tableName);
    }

    @Override
    public TableManager getTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!tables.containsKey(tableName)) {
            return null;
        } else {
            TableManager table = tables.get(tableName);
            if (table == null) {
                tables.put(tableName, new TableManager(getTablePath(tableName).toFile()));
            }
            return tables.get(tableName);
        }
    }

    @Override
    public TableManager createTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else {
            try {
                if (!containsTable(tableName)) {
                    Path tablePath = getTablePath(tableName);
                    Files.createDirectory(tablePath);
                    TableManager table = new TableManager(tablePath.toFile());
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
                forgetCurrentTable();
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
                currentTable = new TableManager(getTablePath(tableName).toFile());
                return 0;
            } else {
                boolean currentAndNewTablesAreDistinct = !currentTable.getName().equals(tableName);
                if (currentAndNewTablesAreDistinct) {
                    if (currentTable.uncommittedChanges() == 0) {
                        currentTable = new TableManager(getTablePath(tableName).toFile());
                        return 0;
                    } else {
                        return currentTable.uncommittedChanges();
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
                tables.put(tableName, new TableManager(getTablePath(tableName).toFile()));
            }
            res.put(tableName, tables.get(tableName).size());
        }
        return res;
    }

    public void forgetCurrentTable() {
        currentTable = null;
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
