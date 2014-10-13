package ru.fizteh.fivt.students.sautin1.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Table provider class.
 * Created by sautin1 on 10/12/14.
 */
public abstract class GeneralTableProvider<MappedValue, T extends GeneralTable<MappedValue>> {
    private Map<String, T> tableMap;
    private final Path rootDir;
    protected final boolean autoCommit;
    private final TableIOTools<MappedValue, T> tableIOTools;

    public GeneralTableProvider(Path rootDir, boolean autoCommit, TableIOTools<MappedValue, T> tableIOTools) {
        if (rootDir == null) {
            throw new IllegalArgumentException("Wrong directory");
        }
        if (tableIOTools == null) {
            throw new IllegalArgumentException("Wrong TableIOTools instance");
        }
        this.rootDir = rootDir;
        tableMap = new HashMap<>();
        this.tableIOTools = tableIOTools;
        this.autoCommit = autoCommit;
    }

    protected GeneralTableProvider(Path rootDir, TableIOTools<MappedValue, T> tableIOTools) {
        this(rootDir, true, tableIOTools);
    }

    /**
     * Getter of rootDir field.
     * @return rootDir field.
     */
    public Path getRootDir() {
        return rootDir;
    }

    /**
     * Create a new table instance.
     * @param name - name of the new table.
     * @return new table instance.
     */
    public abstract T establishTable(String name);

    /**
     * Getter of the table with given name.
     * @param name - name of the table.
     * @return table with corresponding name.
     */
    public T getTable(String name) {
        return tableMap.get(name);
    }

    /**
     * Create new table with given name.
     * @param name - name of the new table.
     * @return new table.
     */
    public T createTable(String name) {
        if (tableMap.get(name) != null) {
            return null;
        }

        T newTable = establishTable(name);
        tableMap.put(name, newTable);

        // create directory

        return newTable;
    }

    /**
     * Remove table by its name.
     * @param name - name of the table.
     */
    public void removeTable(String name) {
        T oldTable = tableMap.remove(name);
        if (oldTable == null) {
            throw new IllegalArgumentException("Table " + name + " doesn't exist");
        }
        // delete files recursively
    }

    /**
     * Load table from the file.
     * @param root - path to the root directory.
     * @param tableName - name of the table to load.
     * @throws IOException if any IO error occured.
     */
    public void loadTable(Path root, String tableName) throws IOException {
        T table = tableMap.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Wrong table name");
        }
        tableIOTools.readTable(root, table);
    }

    /**
     * Save table to file.
     * @param root - path to the root directory.
     * @param tableName - name of the table to save.
     */
    public void saveTable(Path root, String tableName) {
        T table = tableMap.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " doesn't exist");
        }
        tableIOTools.writeTable(root, table);
    }
}
