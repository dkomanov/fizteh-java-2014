package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.
        junit.util.ExitException;

public class TableHolder implements TableProvider {
    private Path rootPath;
    private Map<String, DBTable> tableMap;
    public TableHolder(String rootDir) {
        try {
            rootPath = Paths.get(rootDir);
            if (!rootPath.toFile().exists()) {
                rootPath.toFile().mkdir();
            }
            if (!rootPath.toFile().isDirectory()) {
                throw new IllegalArgumentException(rootDir + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        File rootDirectory = rootPath.toFile();
        String[] childDirectories = rootDirectory.list();
        for (String s: childDirectories) {
            File currentDir = new File(rootPath.toAbsolutePath().toString()
                    + File.separator + s);
            if (!currentDir.isDirectory()) {
                throw new IllegalArgumentException("Child directories are not directories");
                //System.err.println("Child directories are not directories");
                //System.exit(1);
            } else {
                String tableName = currentDir.getName();
                tableMap.put(tableName, new DBTable(rootPath, tableName));
            }
        }
    }
    public Map<String, DBTable> getTableMap() {
        return tableMap;
    }

    public void close() {
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            entry.getValue().close();
        }
        tableMap.clear();
    }

    @Override
    public Table getTable(String name) {
        if (name == null || name.contains(".")||name.contains("\\")||name.contains("/")) {
            throw new IllegalArgumentException("Table name is null or invalid");
        }
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return tableMap.get(tableName);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name) {
        if (name == null || name.contains(".")||name.contains("\\")||name.contains("/")) {
            throw new IllegalArgumentException("Table name is null or invalid");
        }
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return null;
        } else {
            //String pathTableDirectory = rootPath.toAbsolutePath().toString()
              //      + File.separator
               //     + tableName;
            Path pathTableDirectory = rootPath.resolve(tableName);
            File tableDirectory = pathTableDirectory.toFile();
            //File tableDirectory = new File(pathTableDirectory);
            if (!tableDirectory.mkdir()) {
                System.err.println("Can't create table directory");
                System.exit(1);
            } else {
                DBTable newTable = new DBTable(rootPath, tableName, new HashMap<>());
                tableMap.put(tableName, newTable);
                return newTable;
            }
        }
        return null;
    }

    @Override
    public void removeTable(String name) {
        if (name == null || name.matches(".*[\\\\/\\.]+.*")) {
            throw new IllegalArgumentException("Table name is not suitable");
        }
        String tableName = name;
        if (!tableMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " doesn't exist");
        } else {
            Path tableDirectory = tableMap.get(tableName).getDBTablePath();
            String [] subDirs = tableDirectory.toFile().list();
            if (subDirs.length != 0) {
                File[] subDirectories = tableDirectory.toFile().listFiles();
                for (File directory : subDirectories) {
                    try {
                        File[] directoryFiles = directory.listFiles();
                        for (File file : directoryFiles) {
                            try {
                                Files.delete(file.toPath());
                            } catch (IOException | SecurityException e) {
                                System.err.println(e);
                                System.exit(1);
                            }
                        }
                        Files.delete(directory.toPath());
                    } catch (IOException | SecurityException e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                }
            }
            try {
                Files.delete(tableDirectory);
                tableMap.remove(tableName);
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
        }
    }
}
