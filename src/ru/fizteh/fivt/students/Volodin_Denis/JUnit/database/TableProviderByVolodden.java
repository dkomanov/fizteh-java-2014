package ru.fizteh.fivt.students.Volodin_Denis.JUnit.database;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ErrorFunctions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TableProviderByVolodden implements TableProvider {

    private String dbPath;
    private Set<String> tables;
    
    public TableProviderByVolodden(String dir) throws Exception {
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
        Table dbTable;
        try {
            dbTable = new TableByVolodden(WorkWithFile.get(dbPath, name).toString());
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
        Table dbTable = null;
        if (WorkWithFile.exists(dbPath, name)) {
            if (!WorkWithFile.isDirectory(dbPath, name)) {
                ErrorFunctions.tableNameIsFile("create", name);
            }
        } else {
            boolean exists = false;
            try {
                exists = WorkWithFile.createDirectory(dbPath, name);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if (exists) {
                try {
                    dbTable = new TableByVolodden(Paths.get(dbPath, name).normalize().toFile().toString());
                    tables.add(name);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                ErrorFunctions.notMkdir("create", name);
            }
        }
        return dbTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (!WorkWithFile.exists(dbPath, name)) {
            throw new IllegalStateException("[" + name + "] not exists");
        }
        if (!WorkWithFile.getParentName(dbPath, name).equals(WorkWithFile.getFileName(dbPath))) {
            ErrorFunctions.notExists("drop", name);
        }
        if (!WorkWithFile.isDirectory(dbPath)) {
            ErrorFunctions.notDirectory("drop", name);
        }
        
        try {
            recursiveDrop(WorkWithFile.get(dbPath));
            tables.remove(name);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
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
