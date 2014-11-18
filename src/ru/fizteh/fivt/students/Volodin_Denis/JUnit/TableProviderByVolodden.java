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
        dbPath = WorkWithFile.toAbsolutePath(dir);   
        if (!WorkWithFile.exists(dbPath)) {
            WorkWithFile.createDirectory(dbPath);
        }
        tables = readOldTables(Paths.get(dbPath));
    }
    
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (!WorkWithFile.exists(dbPath, name)) {
            ErrorFunctions.notExists("get table", name);
        }
        if (!WorkWithFile.getFileName(dbPath, name).equals(WorkWithFile.getFileName(dbPath))) {
            ErrorFunctions.notExists("get table", name);
        }
        Table dbTable;
        try {
            dbTable = new TableByVolodden(WorkWithFile.get(dbPath, name));
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
        if (WorkWithFile.exists(dbPath, name)) {
            if (WorkWithFile.isDirectory(dbPath, name)) {
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
            if (WorkWithFile.mkdir(dbPath, name)) {
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
                        if (WorkWithFile.isDirectory(pathToFile.toString(), name)) {
                            recursiveDrop(Paths.get(pathToFile.toString(), name).normalize());
                        } else {
                            if (!WorkWithFile.delete(pathToFile.toString(), name)) {
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
                if (WorkWithFile.isDirectory(path.toString(), name)) {
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
