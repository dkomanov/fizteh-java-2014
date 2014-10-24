package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RootDirectory {

    private Path directPath;

    // This HashMap save tables.
    public HashMap<String, Table> tableList;

    public Table currentTable;

    public Path get() {
        return directPath;
    }

    public void executeExit() throws IOException {
        currentTable.write();
        return;
    }

    public RootDirectory() throws IOException {
        directPath = Paths.get(System.getProperty("fizteh.db.dir"));
        currentTable = null;
        if (!directPath.toFile().exists()) {
            directPath.toFile().mkdir();
        }
        if (!directPath.toFile().isDirectory()) {
            System.err.println("Path is uncorrect.");
        }
    }

    public void TableInizial(Table newTable, String tableName)
            throws IOException {
        tableList.put(tableName, newTable);
    }

    public void use(String tableName) throws IOException {
        currentTable.write();
        currentTable = tableList.get(tableName);
        currentTable.read();
        System.out.println("use tablename");
    }

    public void drop(String tableName) {
        if (!tableList.containsKey(tableName)) {
            System.out.println("tablename not exists");
            return;
        }
        boolean currTable = false;
        if (currentTable.equalityTable(tableList.get(tableName))) {
            currTable = true;
        }
        Table deleteTable = tableList.get(tableName);
        deleteTable.dropTable();
        tableList.remove(tableName);
        if (currTable) {
            currentTable = null;
        }
        System.out.println("dropped");
    }

    public void showTables() {
        Set<String> keys = tableList.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String currentKey = it.next();
            Table currentTable = tableList.get(currentKey);
            System.out.print(currentKey);
            System.out.println(currentTable.get());
        }
    }

    public void executePut(String key, String value)
            throws UnsupportedEncodingException {
        currentTable.tableOperationPut(key, value);
    }

    public void executeGet(String key) throws UnsupportedEncodingException {
        currentTable.tableOperationGet(key);
    }

    public void executeRemove(String key) throws UnsupportedEncodingException {
        currentTable.tableOperationRemove(key);
    }

    public void executeList() {
        currentTable.tableOperationList();
    }
}
