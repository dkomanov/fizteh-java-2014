package ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyTableProvider implements TableProvider {
    private File databaseFile;
    private Map<String, MyTable> tables = new HashMap<String, MyTable>();
    private MyTable currentTable = null;

    MyTableProvider(String dir) throws TableException {
        databaseFile = Utils.toFile(dir);
        load();
    }

    private void load() throws TableException {
        for (File file : databaseFile.listFiles()) {
            String tableName = file.getName().toString();
            Path tablePath = Utils.makePathAbsolute(databaseFile, tableName);
            MyTable table = new MyTable(tablePath.toString());
            tables.put(tableName, table);
        }
    }

    public MyTable currentTable() {
        return currentTable;
    }

    public Set<String> tableNames() {
        return tables.keySet();
    }

    public MyTable getTable(String name) {
        Utils.checkNotNull(name);
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    public MyTable createTable(String name) throws TableException {
        Utils.checkNotNull(name);
        currentTable = null;
        MyTable createdTable = null;
        if (!tables.containsKey(name)) {
            File tableFile = checkAndCreateTableFile(name);
            createdTable = new MyTable(tableFile.toString());
            tables.put(name, createdTable);
        }
        return createdTable;
    }

    public void removeTable(String name) throws TableException {
        Utils.checkNotNull(name);
        currentTable = null;
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("The table does not exist");
        } else {
            delete(name);
            tables.remove(name);
        }
    }

    public MyTable useTable(String name) {
        currentTable = getTable(name);
        return currentTable;
    }

    private void delete(String name) throws TableException {
        File tablePath = Paths.get(databaseFile.toString(), name).toFile();
        for (int i = 0; i < 16; i++) {
            File dirPath = Paths.get(tablePath.toString(), i + ".providerDir").toFile();
            for (int j = 0; j < 16; j++) {
                File filePath = Paths.get(dirPath.toString(), j + ".dat").toFile();
                if (filePath.exists()) {
                    Utils.delete(filePath);
                }
            }
            if (dirPath.exists()) {
                Utils.delete(dirPath);
            }
        }
        if (tablePath.exists()) {
            Utils.delete(tablePath);
        }
    }

    private File checkAndCreateTableFile(String name) throws TableException {
        File file;
        try {
            file = Utils.makePathAbsolute(databaseFile, name).toFile();
        } catch (InvalidPathException e) {
            throw new TableException("init: Incorrect name");
        }
        if (!file.getName().equals(name)) {
            throw new TableException("init: Incorrect name");
        }
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new TableException("init: Incorrect name");
            }
        }
        if (!file.isDirectory()) {
            throw new TableException("init: Incorrect name: Main directory is a file");
        }
        return file;
    }
}
