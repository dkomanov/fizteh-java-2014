package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.exception.RemoveFolderException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DBProvider implements TableProvider {


    protected static final String DIR_EXTENTION = ".dir";
    protected static final String FILE_EXTENTION = ".dat";
    private static Path path;
    private Map<String, DBaseTable> tables;
    private Map<String, Table> basicTables;
    private static final String ILLEGAL_NAME = "Illegal name";



    private static void removeFolder(String removeName) throws RemoveFolderException {

        try {
            File file = Paths.get(removeName).normalize().toFile();
            if (!file.isAbsolute()) {
                file = Paths.get(path.toString(), removeName).normalize().toFile();
            }
            if (!file.exists()) {
                throw new RemoveFolderException();
            }
            if (file.isFile()) {
                if (!file.delete()) {
                    throw new RemoveFolderException();
                }
            } else {
                if (!rmRec(file)) {
                    throw new RemoveFolderException();
                }
            }
        } catch (SecurityException e) {
            throw new RemoveFolderException(e);
        }
    }

    private static boolean rmRec(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!rmRec(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }


    public DBProvider(String dir) {
        DBaseTable usingTable;
        path = Paths.get(dir);
        if (!path.toFile().exists()) {
            path.toFile().mkdir();
        }
        if (!path.toFile().isDirectory()) {
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
                    p = tables.get(curDir).getPath().resolve(i + DIR_EXTENTION);
                    s = String.valueOf(j);
                    s = s.concat(FILE_EXTENTION);
                    p = p.resolve(s);
                    if (p.toFile().exists()) {
                        try {
                            tables.get(curDir).getTableDateBase()[i][j] = new DBaseTableChunk(p.toString());
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
            return basicTables.get(name);
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
            if (!tables.containsKey(name)) {
                tables.put(name, new DBaseTable(name, path));
                basicTables.put(name, new DBaseTable(tables.get(name)));
                Path newPath = path.resolve(name);
                newPath.toFile().mkdir();
                } else {
                    throw new IllegalArgumentException("File already exists");
                }
        } catch (SecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException(ILLEGAL_NAME, e);
        }
        return basicTables.get(name);
    }

   @Override
    public void removeTable(final String name) {
        if (!basicTables.containsKey(name)) {
            throw new IllegalArgumentException(ILLEGAL_NAME);
        }
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("File not exists");
        } else {
            Path newPath = path.resolve(name);
            removeFolder(newPath.toString());
            tables.remove(name);
            basicTables.remove(name);
        }
    }

    public Set<Map.Entry<String, DBaseTable>> entrySet() {
        return tables.entrySet();
    }

}
