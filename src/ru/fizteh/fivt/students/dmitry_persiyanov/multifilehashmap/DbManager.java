package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.parser.CommandsParser;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
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
            currentTable = null;
        }
    }

    public File getRootDir() {
        return rootDir;
    }

    public TableManager getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(final String tableName) throws IOException {
        currentTable = new TableManager(getTablePath(tableName));
    }

    public void forgetCurrentTable() { currentTable = null; }

    public Set<String> getTableNames() {
        return tableNames;
    }

    public Path getTablePath(final String path) throws IOException {
        return Paths.get(rootDir.getCanonicalPath(), path).normalize();
    }

    private void loadTableNames() {
        String[] tables = rootDir.list();
        for (String tableName : tables) {
            tableNames.add(tableName);
        }
    }
}