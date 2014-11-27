package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private Map<String, Table> tables;
    /**
     * The table we are working on now.
     */
    private Table currentTable;
    /**
     * Variable of Path type - a path to the main directory.
     */
    private Path dbPath;

    /**
     * Resolve the given path against this path of main database.
     * @param other The path to resolve against this path.
     * @return The resulting path.
     */
    public Path resolvePathAgainstDBPath(final String other) {
        return dbPath.resolve(other);
    }

    /**
     * Return a set from map like entrySet().
     * @return A Set view of the mappings contained in this map.
     */
    public Set<Map.Entry<String, Table>> getSet() {
        return tables.entrySet();
    }

    /**
     * Return a Table (value of map) by key like get().
     * @param key A string - name of table.
     * @return The value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public Table getTable(final String key) {
        return tables.get(key);
    }

    /**
     * Puts a new pair of key and value in map.
     * @param key A string - name of table.
     * @param table A table - variable of Table type.
     * @return The previous value associated with key, or null if there was no mapping for key.
     */
    public Table putKeyAndValue(final String key, final Table table) {
        return tables.put(key, table);
    }

    /**
     * Return a table from a map by getting key like remove().
     * @param key A string - name of table.
     * @return The previous value associated with key, or null if there was no mapping for key.
     */
    public Table removeTable(final String key) {
        return tables.remove(key);
    }

    /**
     * Setting a table for a class.
     * @param table The table, we take for a current.
     */
    public void setTable(final Table table) {
        currentTable = table;
    }

    /**
     * Check, weather a table exists.
     * @return True is not null, false otherwise.
     */
    public boolean currentTableExists() {
        return (currentTable == null);
    }

    /**
     * Removes the mapping for the specified key from this map if present (HashMap's part of Table class).
     * @param key Whose mapping is to be removed from the map.
     * @return The previous value associated with key, or null if there was no mapping for key.
     */
    public String removeKeyAndValueFromCurrentTable(final String key) {
        return currentTable.remove(key);
    }

    /**
     * Returns a Set view of the keys contained in this map (HashMap's part of Table class).
     * @return A set view of the keys contained in this map.
     */
    public Set<String> getSetFromCurrentTable() {
        return currentTable.keySet();
    }

    /**
     * Associates the specified value with the specified key in this map. The old value is replaced.
     * @param key A key which is in a map.
     * @param value A value which is in a map.
     * @return The previous value associated with key, or null if there was no mapping for key.
     */
    public String putKeyAndValueIntoCurrentTable(final String key, final String value) {
        return currentTable.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     * @param key A key which is in a map.
     * @return The value to which the specified key is mapped, or null if this map contains no mapping for the key.
     */
    public String getValueFromCurrentTable(final String key) {
        return currentTable.get(key);
    }

    /**
     * Constructor which assigns a dbPath field and makes a HashMap tables. Then we start reading.
     * @param inputPath The Path to database.
     */
    public DataBase(final Path inputPath) {
        tables = new HashMap<>();
        dbPath = inputPath;
        try {
            read();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Reading from database (it contains 16 directories, each contains 16 files), by using read from class Table
     * @throws IOException Exception In case if we can not read from a table.
     * @throws IllegalArgumentException Exception In case if we can not read from a table.
     */
    public void read() throws IOException, IllegalArgumentException {
        if (dbPath == null) {
            throw new IllegalArgumentException("no database path");
        } else if (!Files.exists(dbPath)) {
            throw new IllegalArgumentException("wrong database path: '" + dbPath.toString() + "'");
        }
        File currentDirectory = new File(dbPath.toString());
        File[] content = currentDirectory.listFiles();
        if (content != null) {
            for (File item: content) {
                if (Files.isDirectory(item.toPath())) {
                    Table table = new Table(item.toPath());
                    table.read();
                    tables.put(item.getName(), table);
                }
            }
        }
        //Else directory is empty.
        currentTable = null;
    }

    /**
     * Writes to database from tables, by using write from class Table.
     * @throws IOException In case if we can not write to a table.
     */
    public void write() throws IOException {
        deleteDirectoryContent();
        for (HashMap.Entry<String, Table> entry: tables.entrySet()) {
            entry.getValue().write();
        }
    }

    /**
     * First we delete the database content.
     * If dbPath points at not a directory, we delete it. Else use next function.
     * @throws RuntimeException In case if we can not delete a file by its' path.
     */
    public void deleteDirectoryContent() throws RuntimeException {
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
    private void deleteDirectory(final Path path) throws RuntimeException {
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
