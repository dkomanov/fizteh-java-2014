package ru.fizteh.fivt.students.SukhanovZhenya.MulriFileHashMap;

import java.io.File;

public class MultiFileHashMap {
    static File currentDir;
    static FileMap currentFileMap;
    static String currentTableName = null;

    MultiFileHashMap(String dirPath) {
        if (dirPath == null) {
            System.err.println("There is no path for dir");
            System.exit(1);
        }
        currentDir = new File(dirPath);
        currentFileMap = null;

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
            currentFileMap = null;
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
        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (tmp.exists()) {
            System.out.println("using " + name);
            currentFileMap = new FileMap(currentDir.getAbsolutePath() + "/" + name);
            currentFileMap.getFile();
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
        if (currentDir == null || currentFileMap == null) {
            System.out.println("no table");
            return;
        }
        currentFileMap.add(key, value);
//        currentFileMap.appendFile();
    }

    void get(String key) {
        if (currentDir == null || currentFileMap == null) {
            System.out.println("no table");
            return;
        }

        currentFileMap.get(key);
    }

    void remove(String key) {
        if (currentDir == null || currentFileMap == null) {
            System.out.println("no table");
            return;
        }

        currentFileMap.remove(key);
//        currentFileMap.appendFile();
    }

    void list() {
        if (currentDir == null || currentFileMap == null) {
            System.out.println("no table");
            return;
        }

        currentFileMap.list();
    }

    void writeFile() {
        if (currentDir == null || currentFileMap == null) {
            return;
        }

        currentFileMap.appendFile();
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

    private boolean checkName(String[] name) {
        boolean result = true;
        result = name.length == 1;
        result = result && name[0].length() < 3;
        if (name[0].length() == 2) {
            result = result && name[0].charAt(0) == '1';
            result = result && Character.getNumericValue(name[0].charAt(1)) < 16;
        }

        return result;
    }
}
