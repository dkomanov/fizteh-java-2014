package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableProviderByVolodden implements TableProvider {

    private String dbPath;
    private Set<String> tables;
    
    TableProviderByVolodden(String dir) throws Exception {
        dbPath = Paths.get(dir).toAbsolutePath().toString();   
        if (!Paths.get(dbPath).toFile().exists()) {
            Files.createDirectory(Paths.get(dbPath));
        }
        tables = readOldTables(Paths.get(dbPath));
    }
    
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (!Paths.get(dbPath, name).normalize().toFile().exists()) {
            ErrorFunctions.notExists("get table", name);
        }
        if (!Paths.get(dbPath, name).getParent().getFileName().toString().equals(
                Paths.get(dbPath).getFileName().toString())) {
            ErrorFunctions.notExists("get table", name);
        }
        Table dbTable;
        try {
            dbTable = new TableByVolodden(Paths.get(dbPath, name).normalize().toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return dbTable;
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            ErrorFunctions.nameIsNull("create", name);
        }
        Table dbTable;
        if (Paths.get(dbPath, name).normalize().toFile().exists()) {
            if (Paths.get(dbPath, name).normalize().toFile().isDirectory()) {
                System.out.println(name + " exists");
                try {
                    dbTable =  new TableByVolodden(name);
                    return dbTable;
                } catch (Exception exception) {
                    return null;
                }
            } else {
                ErrorFunctions.tableNameIsFile("create", name);
            }
        } else {
            if (Paths.get(dbPath, name).normalize().toFile().mkdir()) {
                System.out.println("created");
            } else {
                ErrorFunctions.notMkdir("create", name);
            }
        }
        try {
            dbTable = new TableByVolodden(name);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        tables.add(name);
        return dbTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        Path pathToFile = Paths.get(dbPath, name).normalize();
        if (!pathToFile.toFile().exists()) {
            throw new IllegalStateException("[" + name + "] not exists");
        }
        if (!pathToFile.getParent().getFileName().toString().equals(
                Paths.get(dbPath).getFileName().toString())) {
            ErrorFunctions.notExists("drop", name);
        }
        if (!pathToFile.toFile().isDirectory()) {
            ErrorFunctions.notDirectory("drop", name);
        }
        
        try {
            recursiveDrop(pathToFile);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
        tables.remove(name);
        System.out.println("dropped");
    }

    @Override
    public String[] showTables() {
        try {
            String[] keys = new String[tables.size()];
            int i = -1;
            for (String key : tables) {
                keys[++i] = key;
            }
            return keys;
        } catch (Exception exception) {
            return null;
        }
    }
    
    private static void recursiveDrop(final Path pathToFile) throws Exception {
        if (!pathToFile.toFile().exists()) {
            return;
        }
        if (pathToFile.toFile().isFile()) {
            if (!pathToFile.toFile().delete()) {
                ErrorFunctions.smthWrong("drop");
            }
        }
        try {
            if (pathToFile.toFile().isDirectory()) {
                String[] names = pathToFile.toFile().list();
                if (names.length != 0) {
                    for (String name : names) {
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isDirectory()) {
                            recursiveDrop(Paths.get(pathToFile.toString(), name).normalize());
                        } else {
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
        } catch (SecurityException secException) {
            ErrorFunctions.security("drop", secException.getMessage());
        }
    }
    
    private Set<String> readOldTables(final Path path) throws Exception {
        Set<String> setOfNames = new HashSet<String>();
        String[] names = path.toFile().list();
        try {
            for (String name : names) {
                if (Paths.get(path.toString(), name).toAbsolutePath().toFile().isDirectory()) {
                    try {
                        Table temp = new TableByVolodden(name);
                        temp.size();
                        setOfNames.add(name);
                    } catch (Exception exception) {
                    }
                } else {
                    Files.delete(Paths.get(dbPath, name));
                }
            }
            return setOfNames;
        } catch (Exception exception) {
            ErrorFunctions.smthWrong("read");
            return null;
        }
    }

}
