package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

public class Table {
    private String dbPath;
    private DataBase dbTable;
    
    Table() {
        dbPath = null;
        dbTable = null;
    }
    
    public void setPath(String path) throws Exception {
        dbPath = path;
        if (dbPath == null) {
            System.err.println("Path was not found.");
            System.exit(ReturnCodes.ERROR);
        }
        if (!Paths.get(dbPath).isAbsolute()) {
            dbPath = Paths.get(dbPath).toAbsolutePath().normalize().toString();
        }
        if (!Paths.get(dbPath).normalize().toFile().exists()) {
            if (Paths.get(dbPath).normalize().getParent().toFile().exists()) {
                Files.createDirectory(Paths.get(dbPath).normalize().getFileName());
            } else {
                System.err.println("Directory [" + Paths.get(dbPath).normalize().getParent().getFileName()
                                                 + "] does not exist.");
                System.exit(ReturnCodes.ERROR);
            }
        }
        if (!Paths.get(dbPath).normalize().toFile().isDirectory()) {
            System.err.println("Path [" + dbPath + "] is not a directory.");
            System.exit(ReturnCodes.ERROR);
        }
    }
    
    public void setTable(DataBase table) throws Exception {
        dbTable = table;
    }

    public String getPath() throws Exception {
        return dbPath;
    }
    
    public DataBase getTable() throws Exception {
        return dbTable;
    }
    
    public void create(String arg, HashMap<String, Integer> dbInformation) throws Exception {
        if (Paths.get(dbPath, arg).normalize().toFile().exists()) {
            if (Paths.get(dbPath, arg).normalize().toFile().isDirectory()) {
                System.out.println(arg + " exists");
            } else {
                ErrorFunctions.tableNameIsFile("create", arg);
            }
        } else {
            if (Paths.get(dbPath, arg).normalize().toFile().mkdir()) {
                System.out.println("created");
            } else {
                ErrorFunctions.notMkdir("create", arg);
            }
        }
        dbInformation.put(Paths.get(dbPath, arg).normalize().getFileName().toString(), 0);
    }
    
    public void drop(String arg, HashMap<String, Integer> dbInformation) throws Exception {

        try {
            Path pathToFile = Paths.get(dbPath, arg).normalize();
            if (!pathToFile.toFile().exists()) {
                System.out.println("[" + arg + "] not exists");
                return;
            }
            if (dbTable != null) {
                if (pathToFile.getFileName().toString().equals(dbTable.getPath())) {
                    dbTable.close();
                    dbTable = null;
                }
            }
            
            if (!pathToFile.toFile().isDirectory()) {
                ErrorFunctions.notDirectory("drop", arg);
            }
            recursiveDrop(pathToFile);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        
        dbInformation.remove(Paths.get(dbPath, arg).normalize().getFileName().toString());
        System.out.println("dropped");
    }
    
    private static void recursiveDrop(final Path pathToFile) throws Exception {
        try {
            if (!pathToFile.toFile().exists()) {
                ErrorFunctions.notExists("drop", pathToFile.toString());
                return;
            }
            if (pathToFile.toFile().isFile()) {
                if (!pathToFile.toFile().delete()) {
                    ErrorFunctions.smthWrong("drop");
                }
            }
            if (pathToFile.toFile().isDirectory()) {
                String[] names = pathToFile.toFile().list();
                if (names.length != 0) {
                    for (String name : names) {
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isDirectory()) {
                            recursiveDrop(Paths.get(pathToFile.toString(), name).normalize());
                        }
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isFile()) {
                            if (!Paths.get(pathToFile.toString(), name).normalize().toFile().delete()) {
                                ErrorFunctions.smthWrong("drop");
                            }
                        }
                    }
                }
                if (!pathToFile.toFile().delete()) {
                    ErrorFunctions.smthWrong("drop");
                }
            }
        } catch (InvalidPathException invException) {
            ErrorFunctions.invalidName("drop", invException.getMessage());
        } catch (SecurityException secException) {
            ErrorFunctions.security("drop", secException.getMessage());
        }
    }

    public void use(String arg, HashMap<String, Integer> dbInformation) throws Exception {
        if (!Paths.get(dbPath, arg).normalize().toFile().exists()) {
            System.out.println(arg + " not exists");
        } else {
            if (dbTable != null) {
                if (dbTable.getPath().equals(Paths.get(dbPath, arg).normalize().getFileName().toString())) {
                    System.out.println("is already in use");
                    return;
                }
                dbInformation.remove(dbTable.getPath());
                dbInformation.put(dbTable.getPath(), dbTable.list().length);
                dbTable.close();
            }
            dbTable = new DataBase(Paths.get(dbPath, arg).normalize().toString());
            System.out.println("using " + arg);
        }
    }

    public void showTables(String arg, HashMap<String, Integer> dbInformation) throws Exception {
        Set<String> tables = dbInformation.keySet();
        if (!tables.isEmpty()) {
            System.out.println("table_name row_count");
        }
        for (String table : tables) {
            if (dbTable != null) {
                if (table.equals(dbTable.getPath())) {
                    System.out.println(table + " " + dbTable.list().length);
                } else {
                    System.out.println(table + " " + dbInformation.get(table));
                }
            } else {
                System.out.println(table + " " + dbInformation.get(table));
            }
        }
    }
    
    public void exit() throws Exception {
        if (dbTable != null) {
            dbTable.close();
        }
        System.exit(ReturnCodes.SUCCESS);
    }
    
    public void put(String key, String value) throws Exception {
        String oldValue = dbTable.get(key);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
        dbTable.put(key, value);
    }

    public void get(String key) throws Exception {
        String value = dbTable.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
   
    public void remove(String key) throws Exception {
        String value = dbTable.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            dbTable.remove(key);
            System.out.println("removed");
        }
    }

    public void list() throws Exception {
        String[] keys = dbTable.list();
        if (keys.length == 0) {
            System.out.println("");
        } else {
            for (int i = 0; i + 1 < keys.length; ++i) {
                System.out.print(keys[i] + ", ");
            }
            System.out.println(keys[keys.length - 1]);
        }
    }
}
