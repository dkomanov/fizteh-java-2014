package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableOperations {

    private static final int MAX_TABLES_NUMBER = 16;
    private String multiFileMapDirectoryPath;
    private MultiFileTable currentTable = null;
    HashMap<String, MultiFileTable> tables =
            new HashMap<String, MultiFileTable>();

    public MultiFileHashMapTableOperations(final String directory) {
        if ((directory == null)
                || (directory.equals(""))) {
            throw new IllegalArgumentException(
                    "Directory's name can not be empty!");
        }
        File multiFileMapDirectory = new File(directory);
        if (!multiFileMapDirectory.exists()) {
            throw new IllegalArgumentException(
                    "Directory does not exist!");
        }
        if (multiFileMapDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "Directory should not be a file!");
        }

        this.multiFileMapDirectoryPath =
                multiFileMapDirectory.getAbsolutePath();
        multiFileMapDirectory = new File(multiFileMapDirectoryPath);
        for (final File tableFile : multiFileMapDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }
            MultiFileTable table = new MultiFileTable(
                    multiFileMapDirectoryPath, tableFile.getName());
            tables.put(table.getName(), table);
        }
    }

    public final ATable getTable(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Table's name can not be empty!");
        }

        MultiFileTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        currentTable = table;
        return table;
    }

    public final ATable createTable(final String name) {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException(
                    "Table's name can not be empty!");
        }

        if (tables.size() == MAX_TABLES_NUMBER) {
            throw new IllegalStateException("Too many tables!");
        }

        if (tables.containsKey(name)) {
            return null;
        }

        File tableDirectory = new File(multiFileMapDirectoryPath, name);
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }
        MultiFileTable table = new MultiFileTable(
                multiFileMapDirectoryPath, name);
        tables.put(name, table);
        return table;
    }

    public static final void deleteFile(final File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file : fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }

    public final void removeTable(final String name) {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException(
                    "Table's name cannot be empty");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(
                    String.format("%s not exists", name));
        }

        tables.remove(name);

        File tableFile = new File(multiFileMapDirectoryPath, name);
        deleteFile(tableFile);
    }

    public final void showTables() {
        for (final Map.Entry<String, MultiFileTable> map: tables.entrySet()) {
            System.out.println(map.getKey() + ' ' + map.getValue().size());
        }
    }

    public final void exit() throws IOException {
        if (currentTable == null) {
            return;
        } else {
            currentTable.save();
        }
    }
}
