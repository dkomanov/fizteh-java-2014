package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.JUnit.DBaseTable;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.FactoryException;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;
import ru.fizteh.fivt.students.ekaterina_pogodina.shell.Rm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TableManager {
    public String currentTable;
    public Path path;
    public BaseTable usingTable;
    public Map<String, BaseTable> tables;
    public Map<String, Table> basicTables;
    public boolean saved = false;

    public TableManager(String dir) throws Exception {
        currentTable = null;
        try {
            path = Paths.get(dir);
            if (!path.toFile().exists()) {
                path.toFile().mkdir();
            }
            if (!path.toFile().isDirectory()) {
                FactoryException.throwPathNameException("path is incorrect");
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
                tables.put(curDir, new BaseTable(curDir, path));
                basicTables.put(curDir, new DBaseTable(tables.get(curDir)));
            } else {
                FactoryException.throwRootDirectoryException("root directory contains non-directory files");
            }
            String s;
            Path p;
            usingTable = new BaseTable();
            for (int i = 0; i <  usingTable.SIZEDIR; i++) {
                for (int j = 0; j < usingTable.SIZEDAT; j++) {
                    p = tables.get(curDir).path.resolve(i + "dir");
                    s = String.valueOf(j);
                    s = s.concat(".dat");
                    p = p.resolve(s);
                    if (p.toFile().exists()) {
                        tables.get(curDir).tableDateBase[i][j] = new DataBase(p.toString());
                    }
                }
            }
        }

    }

    public boolean drop(String name) throws Exception {
        if (name == null) {
            FactoryException.throwNullArgumentException("Table name is null");
        }
        boolean tableContainsKey = tables.containsKey(name);
        if (!tableContainsKey) {
            System.out.println(name + " not exists");
        } else {
            Path newPath = path.resolve(name);
            String[] args = new String[3];
            Rm.run(new String[] {"rm", "-r", newPath.toString()}, true, 2);
            tables.remove(name);
            basicTables.remove(name);
        }
        return tableContainsKey;
    }

    public void create(String name) throws Exception {
        if (name == null) {
            FactoryException.throwNullArgumentException("Table name is null");
        }
        try {
            boolean flag = tables.containsKey(name);
            if (!flag) {
                System.out.println("created");
                tables.put(name, new BaseTable(name, path));
                try {
                    tables.put(name, new BaseTable(name, path));
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
            FactoryException.throwNullArgumentException("Table name is null");
        }
        if (currentTable != null) {
            if (usingTable.puted.size() != 0 || usingTable.removed.size() != 0) {
                FactoryException.throwUsingTableException("unsaved changes");
            }
            if (currentTable.equals(name)) {
                FactoryException.throwUsingTableException(name + " is already using");
            }
        }
        try {
            boolean flag = tables.containsKey(name);
            if (flag) {
                currentTable = name;
                usingTable = tables.get(name);
                System.out.println("using " + name);
                for (int i = 0; i < usingTable.SIZEDIR; i++) {
                    for (int j = 0; j < usingTable.SIZEDAT; j++) {
                        if (!(usingTable.tableDateBase[i][j] == null)) {
                            for (Entry<String, String> pair : usingTable.tableDateBase[i][j].dBase.entrySet()) {
                                usingTable.keys.put(pair.getKey(), pair.getValue());
                            }
                        }
                    }
                }
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
            for (int i = 0; i < usingTable.SIZEDIR; i++) {
                for (int j = 0; j < usingTable.SIZEDAT; j++) {
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
