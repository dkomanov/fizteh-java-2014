package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database implements TableProvider {
    private Map<String, Table> tables;
    private String activeTable;
    private Path databasePath;

    public Database(String dbPath) {
        try {
            databasePath = Paths.get(dbPath);
            if (!Files.exists(databasePath)) {
                Files.createDirectory(databasePath);
            }
            if (!Files.isDirectory(databasePath)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalArgumentException("Illegal database directory name: " + databasePath.toString(), e);
        }
        tables = new HashMap<>();

        try (DirectoryStream<Path> directory = Files.newDirectoryStream(databasePath)) {
            for (Path entry : directory) {
                if (!Files.isDirectory(entry)) {
                    throw new IllegalArgumentException("Database directory contains illegal files");
                }

                Table currentTable = new MultiFileTable(databasePath, entry.getFileName().toString());
                tables.put(entry.getFileName().toString(), currentTable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Table getTable(String name) {
        if (!isNameCorrect(name)) {
            throw new InputMistakeException("Illegal table name: " + name);
        }
        return tables.get(name);
    }

    @Override
    public Table getActiveTable() {
        if (activeTable == null) {
            return null;
        }
        return getTable(activeTable);
    }

    @Override
    public Table createTable(String name) {
        try {
            if (getTable(name) != null) {
                return null;
            }

            Table newTable = new MultiFileTable(databasePath, name);
            tables.put(name, newTable);
            return newTable;
        } catch (IOException e) {
            throw new InputMistakeException("Illegal table name: " + e.getMessage());
        }
    }

    @Override
    public void removeTable(String name) {
        if (getTable(name) == null) {
            throw new TableDoesNotExistException(name);
        }
        if (activeTable != null && activeTable.equals(name)) {
            activeTable = null;
        }
        tables.remove(name);
        try {
            Utils.rm(databasePath.resolve(name));
        } catch (IOException e) {
            throw new RuntimeException("Cannot remove directory (" + name + ") from disk: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getTablesList() {
        List<String> list = new LinkedList<>();
        list.addAll(tables.keySet());
        return list;
    }

    @Override
    public void setActiveTable(String name) {
        if (getTable(name) == null) {
            throw new TableDoesNotExistException(name);
        }
        activeTable = name;
    }

    private boolean isNameCorrect(String name) {
        return (name != null) && !(name.matches(".*[</\"*%|\\\\:?>].*|.*\\."));
    }
}
