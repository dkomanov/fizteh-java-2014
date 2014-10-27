package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands.DbManagerCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.table_loader_dumper.TableLoaderDumper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class DbManager {
    public DbManager(final File rootDir) throws IOException {
        this.rootDir = rootDir;
        loadTableNames();
    }
    private File rootDir;
    private TableManager currentTable;
    private Map<String, Integer> tableNames = new HashMap<>();

    public void dumpCurrentTable() throws IOException {
        if (currentTable != null) {
            currentTable.dump();
        }
    }

    public boolean containsTable(final String tableName) {
        return tableNames.containsKey(tableName);
    }

    public void createTable(final String tableName) throws IOException {
        if (!containsTable(tableName)) {
            Files.createDirectory(getTablePath(tableName));
            tableNames.put(tableName, 0);
        } else {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        }
    }

    public void dropTable(final String tableName) throws IOException {
        if (containsTable(tableName)) {
            if (currentTable != null && currentTable.getTableName().equals(tableName)) {
                forgetCurrentTable();
            }
            tableNames.remove(tableName);
            purgeTable(getTablePath(tableName));
        } else {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        }
    }

    private void purgeTable(final Path tablePath) throws IOException {
        File[] dirs = new File(tablePath.toString()).listFiles();
        for (File dir : dirs) {
            purgeDir(dir);
        }
        Files.delete(tablePath);
    }

    private void purgeDir(final File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
        dir.delete();
    }

    public void useTable(final String tableName) throws IOException {
        if (!tableNames.containsKey(tableName)) {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        } else {
            if (currentTable != null && !currentTable.getTableName().equals(tableName)) {
                updateTableNames();
                dumpCurrentTable();
            }
            currentTable = new TableManager(getTablePath(tableName));
        }
    }

    public Map<String, Integer> showTables() {
        updateTableNames();
        Map<String, Integer> res = new HashMap<>();
        res.putAll(tableNames);
        return res;
    }

    public void forgetCurrentTable() {
        currentTable = null;
    }

    private void updateTableNames() {
        if (currentTable != null) {
            tableNames.put(currentTable.getTableName(), currentTable.getSize());
        }
    }

    public Path getTablePath(final String tableName) throws IOException {
        return Paths.get(rootDir.getCanonicalPath(), tableName).normalize();
    }

    public void executeCommand(final DbCommand cmd) throws IOException, TableIsNotChosenException {
        if (cmd instanceof TableManagerCommand) {
            ((TableManagerCommand) cmd).execute(currentTable);
        } else if (cmd instanceof DbManagerCommand) {
            ((DbManagerCommand) cmd).execute(this);
        }
    }

    private void loadTableNames() throws IOException {
        String[] tables = rootDir.list();
        tableNames.clear();
        for (String tableName : tables) {
            tableNames.put(tableName, TableLoaderDumper.countKeys(Paths.get(rootDir.toString(), tableName)));
        }
    }
}
