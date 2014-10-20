package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.table_loader_dumper.TableLoaderDumper;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class TableManager {
    public final static String DIRS_EXTENSION = ".dir";
    public final static String FILES_EXTENSION = ".dat";
    public final Path tablePath;
    public static final int MAX_DIRS_FOR_TABLE = 16;
    public static final int MAX_FILES_FOR_DIR = 16;
    private List<List<Map<String, String>>> tableHashMap = null;

    public TableManager(final Path TablePath) throws IOException {
        this.tablePath = TablePath.toAbsolutePath().normalize();
        initTableHashMap();
        TableLoaderDumper.loadTable(tablePath, tableHashMap);
    }

    public void dump() throws IOException {
        TableLoaderDumper.dumpTable(tablePath, tableHashMap);
    }

    public List<List<Map<String, String>>> getTableHashMap() {
        return tableHashMap;
    }

    public String getTableName() {
        return tablePath.getFileName().toString();
    }

    public static int keyValueDirNum(final String key) {
        byte b = key.getBytes()[0];
        return b % MAX_DIRS_FOR_TABLE;
    }

    public static int keyValueFileNum(final String key) {
        byte b = key.getBytes()[0];
        return b / MAX_DIRS_FOR_TABLE % MAX_FILES_FOR_DIR;
    }

    private void initTableHashMap() {
        tableHashMap = new ArrayList<List<Map<String, String>>>(MAX_DIRS_FOR_TABLE);
        for (int i = 0; i < MAX_DIRS_FOR_TABLE; ++i) {
            tableHashMap.add(new ArrayList<Map<String, String>>(MAX_FILES_FOR_DIR));
            for (int j = 0; j < MAX_FILES_FOR_DIR; ++j) {
                tableHashMap.get(i).add(new HashMap<String, String>());
            }
        }
    }
}