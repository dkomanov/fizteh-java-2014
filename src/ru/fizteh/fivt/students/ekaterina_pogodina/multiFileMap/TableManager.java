package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.shell.Rm;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TableManager {
    public String currentTable;
    public Path path;
    public Table usingTable;
    public Map<String, Table> tables;

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
        String[] tablesList = path.toFile().list();
        for (String curDir : tablesList) {
            Path curTableDirPath = path.resolve(curDir);
            if (curTableDirPath.toFile().isDirectory()) {
                tables.put(curDir, new Table(curDir));
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
                tables.put(name, new Table(name));
                try {
                    tables.put(name, new Table(name));
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
            Table curTable = tables.get(currentTable);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (curTable.tableDateBase[i][j] != null) {
                        curTable.tableDateBase[i][j].close();
                    }
                }
            }
            if (currentTable.equals(name)) {
                throw new Exception(name + " exists");
            }
        }
        try {
            boolean flag = tables.containsKey(name);
            if (flag) {
                currentTable = name;
                usingTable = tables.get(name);
                System.out.println("Using " + name);
            } else {
                System.out.println(name + " not exists");
            }
        } catch (Exception e) {
            throw new Exception("Illegal table name", e);
        }
    }

    public void showTables(String[] args) throws Exception {
        Table entryTable;
        int rowCount;
        for (Map.Entry<String, Table> entry: tables.entrySet()) {
            rowCount = 0;
            entryTable = entry.getValue();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
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
