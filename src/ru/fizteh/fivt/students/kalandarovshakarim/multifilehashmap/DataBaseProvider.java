/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProvider implements TableProvider {

    private HashMap<String, MultiFileTable> tables = null;
    private String directory;

    public DataBaseProvider(String directory) throws IOException {
        this.tables = new HashMap<>();
        this.directory = directory;
        load();
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null cannot be an argument");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException(name + " not found");
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null cannot be an argument");
        }
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException(name + " exists");
        }
        MultiFileTable newTable = null;
        try {
            newTable = new MultiFileTable(directory, name);
        } catch (IOException ex) {
            //nothing
        }
        System.out.println("created");
        tables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null cannot be an argument");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not found");
        } else {
            try {
                tables.get(name).tableUtil.rm(name, true);
                tables.remove(name);
            } catch (IOException ex) {
                //nothing
            }
            System.out.println("dropped");
        }
    }

    public List<String> listTables() {
        return Arrays.asList(tables.keySet().toArray(new String[0]));
    }

    private void load() throws IOException {
        ShellUtils util = new ShellUtils();
        try {
            util.chDir(directory);
        } catch (Exception ex) {
            //nothing
        }
        for (String tableName : util.listFiles()) {
            System.out.println(tableName);
            MultiFileTable table = new MultiFileTable(directory, tableName);
            tables.put(tableName, table);
        }
    }
}
