package ru.fizteh.fivt.students.SukhanovZhenya.Junit;

import java.io.File;
import java.util.List;

public class MultiFileHashMap {
    private static File currentDir;
    private static FileMap currentTable;
    private static String currentTableName = null;

    MultiFileHashMap(String dirPath) {
        if (dirPath == null) {
            System.err.println("There is no path for dir");
            System.exit(1);
        }
        currentDir = new File(dirPath);
        currentTable = null;

        if (!currentDir.exists()) {
            if (!currentDir.mkdir()) {
                System.err.println("Can't create directory");
                System.exit(1);
            }
        }

        if (!checkDir()) {
            System.err.println("Incorrect files!");
            System.exit(1);
        }
    }

    void create(String tableName) {
        File table = new File(currentDir.getAbsolutePath() + "/" + tableName);
        if (table.exists()) {
            System.out.print(tableName + " " + "exists\n");
            return;
        }

        if (!table.mkdir()) {
            System.err.println("Can not create this table");
            System.exit(1);
        }

        System.out.println("created");

    }


    void drop(String name) {
        File fl = new File(currentDir.getAbsolutePath() + "/" + name);
        if (!fl.exists()) {
            System.out.println(name + " not exists");
            return;
        }

        tableDelete(fl.getAbsolutePath());
        System.out.println("dropped");
        if (name.equals(currentTableName)) {
            currentTableName = null;
            currentTable = null;
        }
    }

    private void tableDelete(String path) {
        File tmp = new File(path);
        if (tmp.isDirectory() && tmp.list() != null) {
            for (String name : tmp.list()) {
                tableDelete(tmp.getAbsolutePath() + "/" + name);
            }
        }
        if (!tmp.delete()) {
            System.err.println("Can not delete file");
            System.err.println(tmp.getAbsoluteFile());
            System.exit(1);
        }
    }

    void use(String name) {
        if (currentTable != null && currentTable.changes() > 0) {
            System.err.println(currentTable.changes() + " unsaved changes");
            return;
        }
        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (tmp.exists()) {
            System.out.println("using " + name);
            currentTable = new FileMap(currentDir.getAbsolutePath() + "/" + name);
            currentTable.getFile();
            currentTableName = name;
        } else {
            System.out.println(name + " not exists");
        }
    }

    void showTables() {
        if (currentDir.list() == null) {
            return;
        }

        for (String tableName : currentDir.list()) {
            FileMap tmp = new FileMap(currentDir.getAbsolutePath() + "/" + tableName);
            tmp.getFile();
            System.out.println(tableName + " " + tmp.size());
        }
    }

    void exit() {
        System.exit(0);
    }

    void put(String key, String value) {
        if (currentDir == null || currentTable == null) {
            System.out.println("no table");
            return;
        }
        String newValue = currentTable.put(key, value);
        if (newValue == null) {
            System.out.println("new");
        } else {
            System.out.println("old " + newValue);
        }
    }

    void get(String key) {
        if (currentDir == null || currentTable == null) {
            System.out.println("no table");
            return;
        }

        String found = currentTable.get(key);
        if (found == null) {
            System.out.println("not found");
        } else {
            System.out.println("found " + found);
        }
    }

    int size() {
        if (currentDir == null || currentTable == null) {
            System.out.println("no table");
            return 0;
        }
        return currentTable.size();
    }

    void remove(String key) {
        if (currentDir == null || currentTable == null) {
            System.out.println("no table");
            return;
        }

        String old = currentTable.remove(key);
        if (old == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    void rollback() {
        if (currentDir == null || currentTable == null) {
            return;
        }
        int changes = currentTable.rollback();
        System.out.println(changes);
    }

    void list() {
        if (currentDir == null || currentTable == null) {
            System.out.println("no table");
            return;
        }

        List<String> keys = currentTable.list();
        for (String key : keys) {
            System.out.println(key);
        }
    }

    void commit() {
        if (currentDir == null || currentTable == null) {
            return;
        }

        System.out.println(currentTable.commit());
    }

    private boolean checkDir() {
        if (currentDir.list() == null) {
            return true;
        }
        boolean result = true;
        for (String tableName : currentDir.list()) {
            result = result && checkTable(currentDir.getAbsolutePath() + "/" + tableName);
        }
        return result;
    }

    private boolean checkTable(String pathTable) {
        if (pathTable == null) {
            return true;
        }
        File tableDir = new File(pathTable);
        if (tableDir.exists() && !tableDir.isDirectory())  {
            return false;
        }
        if (tableDir.list() == null) {
            return true;
        }
        boolean result = true;
        for (String dirName : tableDir.list()) {
            result = result && checkFiles(pathTable + "/" + dirName);
        }

        if (result) {
            FileMap tmp = new FileMap(pathTable);
            tmp.getFile();
        }
        return result;
    }

    private boolean checkFiles(String pathFiles) {
        if (pathFiles == null) {
            return true;
        }
        File tableSubDir = new File(pathFiles);
        if (tableSubDir.exists() && !tableSubDir.isDirectory()) {
            return true;
        }

        if (tableSubDir.list() == null) {
            return true;
        }
        boolean result = true;
        for (String fileMap : tableSubDir.list()) {
            result = result && checkHash(tableSubDir.getAbsolutePath() + "/" + fileMap);
        }
        return result;
    }

    private boolean checkHash(String filePath) {
        if (filePath == null) {
            return false;
        }

        File tmp = new File(filePath);

        return tmp.isFile();
    }
}
