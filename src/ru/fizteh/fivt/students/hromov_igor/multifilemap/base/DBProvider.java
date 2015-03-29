package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.hromov_igor.shell.cmd.Rm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DBProvider implements TableProvider {


    private static final String DIR_EXTENTION = ".dir";
    private static final String FILE_EXTENTION = ".dat";
    private String currentTable;
    private Path path;
    private Map<String, DBaseTable> tables;
    private Map<String, Table> basicTables;
    private static final String ILLEGAL_KEY_NAME = "Illegal name of key";
    private static final String ILLEGAL_TABLE_NAME = "Illegal name of table";


    public DBProvider(String dir) {
        DBaseTable usingTable;
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
            for (int i = 0; i <  usingTable.size; i++) {
                for (int j = 0; j < usingTable.size; j++) {
                    p = tables.get(curDir).path.resolve(i + DIR_EXTENTION);
                    s = String.valueOf(j);
                    s = s.concat(FILE_EXTENTION);
                    p = p.resolve(s);
                    if (p.toFile().exists()) {
                        try {
                            tables.get(curDir).tableDateBase[i][j] = new DBaseTableChunk(p.toString());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Can't create new db");
                        }
                    }
                }
            }
        }


    }

    @Override
    public Table getTable(String name) {
        if (basicTables.containsKey(name)) {
            try {
                return basicTables.get(name);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(ILLEGAL_KEY_NAME, e);
            }
        }
        return null;
    }

    @Override
    public Table createTable(final String name) {
        if (basicTables.containsKey(name)) {
            return null;
        }
        try {
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
                    } catch (SecurityException e) {
                        throw new IllegalArgumentException("incorrect name", e);
                    }
                } else {
                    throw new IllegalArgumentException("File already exists");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Illegal table name", e);
            }
            return basicTables.get(name);
        } catch (Exception e) {
            throw new IllegalArgumentException(ILLEGAL_KEY_NAME, e);
        }
    }

   @Override
    public void removeTable(final String name) {
        if (!basicTables.containsKey(name)) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME);
        }
        try {
            if (name == null) {
                throw new IllegalArgumentException();
            }
            boolean tableContainsKey = tables.containsKey(name);
            if (!tableContainsKey) {
                throw new IllegalArgumentException("File not exists");
            } else {
                Path newPath = path.resolve(name);
                Rm.run(new String[]{"rm", "-r", newPath.toString()});
                tables.remove(name);
                basicTables.remove(name);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME, e);
        }
    }

    public Set<Map.Entry<String, DBaseTable>> entrySet() {
        return tables.entrySet();
    }

}
