package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.JUnit.DBaseTable;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.shell.Rm;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TableManager {
    public String currentTable;
    public Path path;
    public BaseTable usingTable;
    public Map<String, BaseTable> tables;
    public Map<String, Table> basicTables;
    private static final int SIZE1 = 16;
    private static final int SIZE2 = 16;

    public TableManager(String dir) throws Exception {
        currentTable = null;
        try {
            path = Paths.get(dir);
            if (!path.toFile().exists()) {
                path.toFile().mkdir();
            }
            if (!path.toFile().isDirectory()) {
                throw new Exception("path is incorrect");
            }
        } catch (Exception e) {

            throw new Exception("path is incorrect", e);
        }
        tables = new HashMap<>();
        basicTables = new HashMap<>();
        String[] tablesList = path.toFile().list();
        for (String curDir : tablesList) {
            Path curTableDirPath = path.resolve(curDir);
            if (curTableDirPath.toFile().isDirectory()) {
                tables.put(curDir, new BaseTable(curDir));
                basicTables.put(curDir, new DBaseTable(tables.get(curDir)));
            } else {
                throw new Exception("root directory contains non-directory files");
            }
        }
    }

    public boolean drop(String name) throws Exception {
        if (name == null) {
            throw new Exception("Table name is null");
        }
        boolean flag = tables.containsKey(name);
        if (!flag) {
            System.out.println(name + " not exists");
        } else {
            Path newPath = path.resolve(name);
            String[] args = new String[3];
            args[0] = "rm";
            args[1] = "-r";
            args[2] = newPath.toString();
            Rm.run(args, true, 2);
            tables.remove(name);
            basicTables.remove(name);
        }
        return flag;
    }

    public void create(String name) throws Exception {
        if (name == null) {
            throw new Exception("Table name is null");
        }
        try {
            boolean flag = tables.containsKey(name);
            if (!flag) {
                System.out.println("created");
                tables.put(name, new BaseTable(name));
                try {
                    tables.put(name, new BaseTable(name));
                    basicTables.put(name, new DBaseTable(tables.get(name)));
                    Path newPath = path.resolve(name);
                    newPath.toFile().mkdir();
                } catch (Exception e) {
                    throw new Exception("incorrect name", e);
                }
            } else {
                System.out.println(name + " already exists");
            }
        } catch (Exception e) {
            throw new Exception("Illegal table name", e);
        }
    }

    public void use(String name) throws Exception {
        if (name == null) {
            throw new Exception("Table name is null");
        }
        if (currentTable != null) {
            BaseTable curTable = tables.get(currentTable);
            for (int i = 0; i < SIZE1; i++) {
                for (int j = 0; j < SIZE2; j++) {
                    if (curTable.tableDateBase[i][j] != null) {
                        curTable.tableDateBase[i][j].close();
                    }
                }
            }
            if (currentTable.equals(name)) {
                throw new Exception(name + " is already using");
            }
        }
        try {
            boolean flag = tables.containsKey(name);
            if (flag) {
                currentTable = name;
                usingTable = tables.get(name);
                System.out.println("using " + name);
            } else {
                System.out.println(name + " not exists");
            }
        } catch (Exception e) {
            throw new Exception("Illegal table name", e);
        }
    }

    public void showTables(String[] args) throws Exception {
        BaseTable entryTable;
        int rowCount;
        for (Map.Entry<String, BaseTable> entry: tables.entrySet()) {
            rowCount = 0;
            entryTable = entry.getValue();
            for (int i = 0; i < SIZE1; i++) {
                for (int j = 0; j < SIZE2; j++) {
                    if (entryTable.tableDateBase[i][j] != null) {
                        rowCount = rowCount + entryTable.tableDateBase[i][j].rowCount();
                    }
                }
            }
            System.out.println(entry.getKey() + " " + rowCount);
        }
    }

    public void missingOperand(String operation) throws Exception {
        throw new Exception(operation + ": missing operand");
    }

    public void manyArgs(String operation) throws Exception {
        throw new Exception(operation + ": too many arguments");
    }

}
