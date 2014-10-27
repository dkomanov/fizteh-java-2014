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
    private HashMap<String, Table> tableList = new HashMap<String, Table>();

    private Table currentTable;

    public Path get() {
        return directPath;
    }

    public boolean checkTableExist(String nameTable) {
        if (tableList.containsKey(nameTable)) {
            return true;
        }
        return false;
    }

    public void executeExit() throws IOException {
        if (currentTable != null) {
            currentTable.write();
        }
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
        String[] list = directPath.toFile().list();
        if (list.length == 0) {
            return;
        }
        for (String tablename : list) {
            Table currentDirTable = new Table(this, tablename, true);
            tableList.put(tablename, currentDirTable);
        }
    }

    public void tableInizial(Table newTable, String tableName)
            throws IOException {
        tableList.put(tableName, newTable);
        newTable.setName(tableName);
    }

    public void use(String tableName, boolean ind) throws IOException {
        if (currentTable != null) {
            currentTable.write();
        }
        currentTable = tableList.get(tableName);
        currentTable.read();
        if (ind) {
            System.out.println("use tablename");
        }
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

    public void showTables() throws IOException {
        Set<String> keys = tableList.keySet();
        Iterator<String> it = keys.iterator();
        Iterator<String> iter = keys.iterator();
        Table functionCurrentTables = currentTable;
        if (currentTable == null) {
            Command.use(this, tableList.get(iter.next()).getName(), false);
            functionCurrentTables = currentTable;
        }
        String currentTablesName = functionCurrentTables.getName();
        while (it.hasNext()) {
            String currentKey = it.next();
            if (currentTablesName.equals(currentKey)) {
                System.out.print(currentKey + " ");
                System.out.println(functionCurrentTables.get());
                tableList.get(currentKey).nullNumberRecords();
                continue;
            }
            Table currTables = tableList.get(currentKey);
            Command.use(this, currentKey, false);
            System.out.print(currentKey + " ");
            System.out.println(currTables.get());
            tableList.get(currentKey).nullNumberRecords();
        }
        Command.use(this, functionCurrentTables.getName(), false);
    }

    public void executePut(String key, String value) throws IOException {
        if (currentTable != null) {
            currentTable.tableOperationPut(key, value);
            return;
        }
        System.out.println("no table");
    }

    public void executeGet(String key) throws UnsupportedEncodingException {
        if (currentTable != null) {
            currentTable.tableOperationGet(key);
            return;
        }
        System.out.println("no table");
    }

    public void executeRemove(String key) throws UnsupportedEncodingException {
        if (currentTable != null) {
            currentTable.tableOperationRemove(key);
            return;
        }
        System.out.println("no table");
    }

    public void executeList() {
        if (currentTable != null) {
            currentTable.tableOperationList();
            return;
        }
        System.out.println("no table");
    }
}
