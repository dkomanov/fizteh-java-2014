package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.hromov_igor.shell.cmd.Rm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TableManager {
    
    public String currentTable;
    public Path path;
    public DBaseTable usingTable;
    public Map<String, DBaseTable> tables;
    public Map<String, Table> basicTables;
    public boolean saved = false;

    public TableManager(String dir) {
        currentTable = null;
        try {
            path = Paths.get(dir);
            if (!path.toFile().exists()) {
                path.toFile().mkdir();
            }
            if (!path.toFile().isDirectory()) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        tables = new HashMap<>();
        basicTables = new HashMap<>();
        String[] tablesList = path.toFile().list();
        for (String curDir : tablesList) {
            Path curTableDirPath = path.resolve(curDir);
            if (curTableDirPath.toFile().isDirectory()) {
                tables.put(curDir, new DBaseTable(curDir, path));
                basicTables.put(curDir, new DBaseTable(tables.get(curDir)));
            } else {
                throw new IllegalArgumentException();
            }
            String s;
            Path p;
            usingTable = new DBaseTable();
            for (int i = 0; i <  usingTable.SIZE; i++) {
                for (int j = 0; j < usingTable.SIZE; j++) {
                    p = tables.get(curDir).path.resolve(i + "dir");
                    s = String.valueOf(j);
                    s = s.concat(".dat");
                    p = p.resolve(s);
                    if (p.toFile().exists()) {
                        try {
                            tables.get(curDir).tableDateBase[i][j] = new DataBase(p.toString());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Can't create new db");
                        }
                    }
                }
            }
        }

    }

    public boolean drop(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        boolean tableContainsKey = tables.containsKey(name);
        if (!tableContainsKey) {
            System.out.println(name + " not exists");
        } else {
            Path newPath = path.resolve(name);
            String[] args = new String[3];
            Rm.run(new String[]{"rm", "-r", newPath.toString()});
            tables.remove(name);
            basicTables.remove(name);
        }
        return tableContainsKey;
    }

    public void create(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            boolean flag = tables.containsKey(name);
            if (!flag) {
                System.out.println("created");
                tables.put(name, new DBaseTable(name, path));
                try {
                    tables.put(name, new DBaseTable(name, path));

                    basicTables.put(name, new DBaseTable(tables.get(name)));
                    Path newPath = path.resolve(name);
                    newPath.toFile().mkdir();
                } catch (Exception e) {
                    throw new IllegalArgumentException("incorrect name", e);
                }
            } else {
                System.out.println(name + " already exists");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal table name", e);
        }
    }

}
