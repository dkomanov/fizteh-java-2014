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
        dbPath = FileUtils.toAbsolutePath(dir);
        if (!FileUtils.exists(dbPath)) {
            FileUtils.createDirectory(dbPath);
        }
        tables = readOldTables(Paths.get(dbPath));
    }
    
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (!FileUtils.exists(dbPath, name)) {
            ErrorFunctions.notExists("get table", name);
        }
        Table dbTable;
        try {
            dbTable = new TableByVolodden(FileUtils.get(dbPath, name).toString());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return dbTable;
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            ErrorFunctions.nameIsNull("create", name);
        }
        Table dbTable = null;
        if (FileUtils.exists(dbPath, name)) {
            if (!FileUtils.isDirectory(dbPath, name)) {
                ErrorFunctions.tableNameIsFile("create", name);
            }
        } else {
            boolean exists = false;
            try {
                exists = FileUtils.createDirectory(dbPath, name);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
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
        if (!FileUtils.exists(dbPath, name)) {
            throw new IllegalStateException("[" + name + "] not exists");
        }
        if (!FileUtils.getParentName(dbPath, name).equals(FileUtils.getFileName(dbPath))) {
            ErrorFunctions.notExists("drop", name);
        }
        if (!FileUtils.isDirectory(dbPath)) {
            ErrorFunctions.notDirectory("drop", name);
        }
        
        try {
            recursiveDrop(FileUtils.get(dbPath));
            tables.remove(name);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
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
                        if (FileUtils.isDirectory(pathToFile.toString(), name)) {
                            recursiveDrop(Paths.get(pathToFile.toString(), name).normalize());
                        } else {
                            if (!FileUtils.delete(pathToFile.toString(), name)) {
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
                if (FileUtils.isDirectory(path.toString(), name)) {
                    Table temp = new TableByVolodden(name);
                    temp.size();
                    setOfNames.add(name);
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
