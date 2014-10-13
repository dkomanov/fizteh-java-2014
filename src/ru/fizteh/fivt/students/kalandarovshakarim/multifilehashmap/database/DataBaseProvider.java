/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProvider implements TableProvider {

    private Map<String, Table> tables = null;
    private final String directory;

    public DataBaseProvider(String directory) throws IOException {
        this.tables = new HashMap<>();
        this.directory = directory;
        load();
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null cannot be an argument");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException(name + " not found");
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null cannot be an argument");
        }
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException(name + " exists");
        }
        Table newTable = null;
        try {
            newTable = new MultiFileTable(directory, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null cannot be an argument");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not found");
        } else {
            ShellUtils utils = new ShellUtils();
            Path tablePath = Paths.get(directory, name);
            try {
                utils.rm(tablePath.toString(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tables.remove(name);
        }
    }

    public List<String> listTables() {
        String[] array = tables.keySet().toArray(new String[0]);
        return Arrays.asList(array);
    }

    private void load() throws IOException {
        String[] list = new File(directory).list();
        for (String tableName : list) {
            Table table = new MultiFileTable(directory, tableName);
            tables.put(tableName, table);
        }
    }
}
