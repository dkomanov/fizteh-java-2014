package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Table {
    private String tableName;
    private ArrayList<SingleTable> tableParts;
    private SingleTable[][] parts;
    private DataBaseConnector databaseConnector;

    public Table(String name, DataBaseConnector dbConnector) throws LoadOrSaveException {
        tableName = new String(name);
        tableParts = new ArrayList<>();
        databaseConnector = dbConnector;
        parts = new SingleTable[16][];
        for (int i = 0; i < 16; ++i) {
            parts[i] = new SingleTable[16];
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                parts[i][j] = new SingleTable(i, j, this);
                tableParts.add(parts[i][j]);
            }
        }
    }

    public SingleTable selectSingleTable(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        if (ndirectory < 0) {
            ndirectory += 16;
        }
        if (nfile < 0) {
            nfile += 16;
        }
        return parts[ndirectory][nfile];
    }


    public void list() {
        Set<String> allKeys = new HashSet<>();
        for (SingleTable table : tableParts) {
            Set<String> keys = table.list();
            allKeys.addAll(keys);
        }
        System.out.println(String.join(", ", allKeys));
    }

    public void get(String key) {
        SingleTable table = selectSingleTable(key);

        String value = table.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    public Path getTableDirectory() {
        return databaseConnector.getRootDirectory().resolve(tableName);
    }

    public void put(String key, String value) throws IOException {
        SingleTable table = selectSingleTable(key);
        String oldValue = table.put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

    public void remove(String key) {
        SingleTable table = selectSingleTable(key);

        String deleted = table.remove(key);

        if (deleted == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public void drop() throws IOException, LoadOrSaveException {
        File tableDirectory = getTableDirectory().toFile();
        for (SingleTable[] directory : parts) {
            for (SingleTable singleTableFile : directory) {
                singleTableFile.drop();
            }
        }
        tableDirectory.delete();
    }

    public String getTableName() {
        return tableName;
    }

    public int size() {
        int size = 0;
        for (SingleTable part : tableParts) {
            size += part.size();
        }
        return size;
    }

    public void save() throws IOException, LoadOrSaveException {
        for (SingleTable singleTable : tableParts) {
            singleTable.save();
        }
    }
}
