package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 22 October 2014
 * Time: 22:53
 */
public class DataBase {
    /**
     * Tables: name + variable of Table class.
     */
    public HashMap<String, Table> tables;
    /**
     * The table we are working on now.
     */
    public Table currentTable;
    /**
     * Variable of Path type - a path to the main directory.
     */
    private Path dbPath;

    /**
     * Function, which can return a private field dbPath.
     * @return Path value - dbPath.
     */
    public Path getDbPath() {
        return dbPath;
    }

    /**
     * Constructor which assigns a dbPath field and makes a HashMap tables. Then we start reading.
     * @param inputPath The Path to database.
     */
    public DataBase(final Path inputPath) {
        tables = new HashMap<>();
        dbPath = inputPath;
        read();
    }

    /**
     * Reading from database (it contains 16 directories, each contains 16 files), by using read from class Table
     */
    public void read() {
        if (dbPath == null || !Files.exists(dbPath)) {
            throw new RuntimeException("wrong database path");
        }
        File currentDirectory = new File(dbPath.toString());
        File[] content = currentDirectory.listFiles();
        if (content != null) {
            for (File item: content) {
                if (Files.isDirectory(item.toPath())) {
                    Table table = new Table(item.toPath());
                    try {
                        table.read();
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                        System.exit(1);
                    }
                    tables.put(item.getName(), table);
                }
            }
        }
        //Else directory is empty.
        currentTable = null;
    }

    /**
     * Writes to database from tables, by using write from class Table.
     */
    public void write() {
        deleteDirectoryContent();
        for (HashMap.Entry<String, Table> entry: tables.entrySet()) {

            try {
                entry.getValue().write();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * First we delete the database content.
     * If dbPath points at not a directory, we delete it. Else use next function.
     */
    public void deleteDirectoryContent() {
        if (!Files.isDirectory(dbPath)) {
            try {
                Files.delete(dbPath);
            } catch (IOException ex) {
                throw new RuntimeException("cannot delete '" + dbPath.toString() + "'");
            }
        } else {
            deleteDirectory(dbPath);
        }
    }

    /**
     * Recursive deletion of directories (we consider them empty because we have read them).
     * @param path A Path to a directory, which should be deleted.
     */
    private void deleteDirectory(final Path path) {
        File currentDirectory = new File(path.toString());
        File[] content = currentDirectory.listFiles();

        if (content != null) {
            for (File item: content) {
                if (item.isFile()) {
                    try {
                        Files.delete(item.toPath());
                    } catch (IOException ex) {
                        throw new RuntimeException("cannot delete file '" + item.toPath().toString() + "'");
                    }
                } else {
                    deleteDirectory(item.toPath());
                }
            }
        }

        if (!path.equals(dbPath)) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new RuntimeException("cannot delete '" + path.toString() + "'");
            }
        }

    }
}
