package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by denny_000 on 08.10.2014.
 */

public class DataBaseCache {
    private Set<TableHash> listOfTables = new HashSet<>();
    public static Path dataBasePath;


    public void init(String dbPath)
            throws DatabaseFileStructureException, WorkWithMemoryException   {
        dataBasePath = Paths.get(System.getProperty(dbPath));
        File dataBaseDirectory = dataBasePath.toFile();
        if (!dataBaseDirectory.isDirectory() || !dataBaseDirectory.exists()) {
            System.err.println("Root is not directory or not exists");
            System.exit(-1);
        }
        try {
            if (dataBaseDirectory.exists() && dataBaseDirectory.isDirectory()) {
                File[] subdirs = getTables(dataBaseDirectory);
                for (File dir : subdirs) {
                    String name = dir.getName();
                    TableHash table = new TableHash(name, dataBasePath);
                    listOfTables.add(table);
                }
            } else {
                throw new DatabaseFileStructureException("Root directory not found");
            }
        } catch (SecurityException ex) {
            throw new WorkWithMemoryException("Error in loading, access denied: "
                    + ex.getMessage(), ex);
        }
    }

    private File[] getTables(File directory) throws DatabaseFileStructureException {
        File[] subfolders = directory.listFiles();
        for (File folder : subfolders) {
            if (!folder.isDirectory()) {
                throw new DatabaseFileStructureException("There is files in root folder. File'"
                        + folder.getName() + "'");
            }
        }
        return subfolders;
    }

    public void commit() {
       for (TableHash table: listOfTables) {
           table.commit();
       }
    }

    public void put(String key, String value, TableHash table) {
        table.put(key, value);
    }
    public void get(String key, TableHash table) {
        table.get(key);
    }

    public void list(TableHash table) {
        table.list();
    }

    public void remove(String key, TableHash table) {
        table.remove(key);
    }

    public void dropTable(String tableName)
            throws InvalidCommandException {
        File table = dataBasePath.resolve(tableName).toFile();
        if (!table.exists()) {
            System.out.println(tableName + " not exists");
        }
        delete(table);
        System.out.println("dropped");
        for (TableHash tableHash: listOfTables) {
            if (tableHash.getTableName().equals(tableName)) {
                listOfTables.remove(tableHash);
            }
        }
    }

    public void createTable(String tableName)
        throws InvalidCommandException {
        File table = dataBasePath.resolve(tableName).toFile();
        if (table.exists()) {
            System.out.println(tableName + " exists");
        }
        mkdir(tableName);
        listOfTables.add(new TableHash(tableName, dataBasePath));
        System.out.println("created");
    }

    public TableHash useTable(String tableName) {
        for (TableHash table: listOfTables) {
            if (table.getTableName().equals(tableName)) {
                System.out.println("using " + tableName);
                return table;
            }
        }
        System.out.println(tableName + " not exists");
        return null;
    }

    public void showTables() {
        System.out.println("table_name row_count");
        for (TableHash table: listOfTables) {
            System.out.println(table.getTableName() + " " + table.getCount());
        }
    }

    private static void mkdir(String name)
            throws InvalidCommandException {
        File curDir = new File(dataBasePath + File.separator + name);
        if (curDir.exists()) {
            errorFileAlreadyExists("mkdir", name);
        } else if (!curDir.mkdir()) {
            errorCreatingFile("mkdir");
        }
    }

    private static void delete(File file)
            throws InvalidCommandException {
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            if (content == null) {
                errorCannotPerform("rm");
                return;
            }
            for (File children : content) {
                delete(children);
            }
        }
        if (!file.delete()) {
            errorCannotPerform("rm");
        }
    }

    private static void errorFileAlreadyExists(String command, String file)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": " + file + ": File already exists");
    }

    private static void errorCreatingFile(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": cannot create file or directory");
    }

    private static void errorCannotPerform(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": cannot perform this operation");
    }
}
