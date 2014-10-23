package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands.DbManagerCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.TableManagerCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

public final class DbManager {
    public DbManager(final File rootDir) {
        this.rootDir = rootDir;
        loadTableNames();
    }
    private File rootDir;
    private TableManager currentTable = null;
    private Set<String> tableNames = new TreeSet<String>();

    public void dumpCurrentTable() throws IOException {
        if (currentTable != null) {
            currentTable.dump();
        }
    }

    public boolean containsTable(final String tableName) {
        return tableNames.contains(tableName);
    }

    public void createTable(final String tableName) throws IOException {
        if (!containsTable(tableName)) {
            Files.createDirectory(getTablePath(tableName));
            tableNames.add(tableName);
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
        if (!tableNames.contains(tableName)) {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        } else {
            if (currentTable != null && !currentTable.getTableName().equals(tableName)) {
                dumpCurrentTable();
            }
            currentTable = new TableManager(getTablePath(tableName));
        }
    }

    public void forgetCurrentTable() { currentTable = null; }

    public Set<String> getTableNames() {
        return tableNames;
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

    private void loadTableNames() {
        String[] tables = rootDir.list();
        for (String tableName : tables) {
            tableNames.add(tableName);
        }
    }
}
