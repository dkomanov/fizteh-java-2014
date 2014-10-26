package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table {
    private final int FILES_CNT = 16;
    private final int FOLDERS_CNT = 16;
    private String tableName;
    private List<SingleTable> tableParts;
    private SingleTable[][] parts;
    private DataBaseConnector databaseConnector;

    public Table(String name, DataBaseConnector dbConnector) throws LoadOrSaveException {
        tableName = new String(name);
        tableParts = new ArrayList<>();
        databaseConnector = dbConnector;
        parts = new SingleTable[FOLDERS_CNT][];
        for (int i = 0; i < FOLDERS_CNT; ++i) {
            parts[i] = new SingleTable[FILES_CNT];
        }
        for (int i = 0; i < FOLDERS_CNT; ++i) {
            for (int j = 0; j < FILES_CNT; ++j) {
                parts[i][j] = new SingleTable(i, j, this);
                tableParts.add(parts[i][j]);
            }
        }
    }

    public SingleTable selectSingleTable(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % FOLDERS_CNT;
        int nfile = hashcode / FOLDERS_CNT % FILES_CNT;
        if (ndirectory < 0) {
            ndirectory += FOLDERS_CNT;
        }
        if (nfile < 0) {
            nfile += FILES_CNT;
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
