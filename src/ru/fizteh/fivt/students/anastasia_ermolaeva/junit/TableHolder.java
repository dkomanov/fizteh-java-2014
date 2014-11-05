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
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Utility;

public class TableHolder implements TableProvider {
    private Path rootPath;
    private Map<String, DBTable> tableMap;

    public TableHolder(final String rootDir) {
        try {
            rootPath = Paths.get(rootDir);
            if (!rootPath.toFile().exists()) {
                rootPath.toFile().mkdir();
            }
            if (!rootPath.toFile().isDirectory()) {
                throw new IllegalArgumentException(rootDir
                        + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir
                    + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        Utility.checkDirectorySubdirs(rootPath);
        for (File currentSubdir : rootPath.toFile().listFiles()) {
                String tableName = currentSubdir.getName();
                tableMap.put(tableName, new DBTable(rootPath, tableName));
        }
    }

    public final Map<String, DBTable> getTableMap() {
        return tableMap;
    }

    public final void close() {
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            entry.getValue().commit();
        }
        tableMap.clear();
    }

    @Override
    public final Table getTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return tableMap.get(tableName);
        } else {
            return null;
        }
    }

    @Override
    public final Table createTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return null;
        } else {
            Path pathTableDirectory = rootPath.resolve(tableName);
            File tableDirectory = pathTableDirectory.toFile();
            if (!tableDirectory.mkdir()) {
                System.err.println("Can't create table directory");
                //throw new ExitException("Can't create table directory", 1);
                System.exit(1);
            } else {
                DBTable newTable = new DBTable(rootPath,
                        tableName, new HashMap<>());
                tableMap.put(tableName, newTable);
                return newTable;
            }
        }
        return null;
    }

    @Override
    public final void removeTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (!tableMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " doesn't exist");
        } else {
            Path tableDirectory = tableMap.get(tableName).getDBTablePath();
            try {
                Files.walkFileTree(tableDirectory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e)
                            throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            /*
                             * Directory iteration failed.
                              */
                            throw e;
                        }
                    }
                });
            } catch (IOException | SecurityException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
