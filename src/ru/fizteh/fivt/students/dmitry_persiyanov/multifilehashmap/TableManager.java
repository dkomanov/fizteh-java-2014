package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.table_loader_dumper.TableLoaderDumper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class TableManager {
    public static final String DIRS_EXTENSION = ".dir";
    public static final String FILES_EXTENSION = ".dat";
    public final Path tablePath;
    public static final int MAX_DIRS_FOR_TABLE = 16;
    public static final int MAX_FILES_FOR_DIR = 16;
    private int size;
    private List<List<Map<String, String>>> tableHashMap;

    public TableManager(final Path tablePath) throws IOException {
        this.tablePath = tablePath.toAbsolutePath().normalize();
        initTableHashMap();
        TableLoaderDumper.loadTable(this.tablePath, tableHashMap);
        recalculateSize();
    }

    public int getSize() {
        return size;
    }

    private void recalculateSize() {
        for (List<Map<String, String>> list : tableHashMap) {
            for (Map<String, String> map : list) {
                size += map.size();
            }
        }
    }

    public void dump() throws IOException {
        TableLoaderDumper.dumpTable(tablePath, tableHashMap);
    }

    public String get(final String key) {
        int dir = keyValueDirNum(key);
        int file = keyValueFileNum(key);
        return tableHashMap.get(dir).get(file).get(key);
    }

    public String put(final String key, final String value) {
        int dir = keyValueDirNum(key);
        int file = keyValueFileNum(key);
        String oldValue = tableHashMap.get(dir).get(file).put(key, value);
        if (oldValue == null) {
            size++;
        }
        return oldValue;
    }

    public List<String> list() {
        List<String> keysList = new LinkedList<>();
        for (List<Map<String, String>> list : tableHashMap) {
            for (Map<String, String> map : list) {
                Set<String> keySet = map.keySet();
                for (String nextKey : keySet) {
                    keysList.add(nextKey);
                }
            }
        }
        return keysList;
    }

    public String remove(final String key) {
        int dir = keyValueDirNum(key);
        int file = keyValueFileNum(key);
        String oldValue = tableHashMap.get(dir).get(file).get(key);
        if (oldValue != null) {
            size--;
        }
        return oldValue;
    }

    public String getTableName() {
        return tablePath.getFileName().toString();
    }

    private static int keyValueDirNum(final String key) {
        int b = key.getBytes()[0] + 128;
        return b % MAX_DIRS_FOR_TABLE;
    }

    private static int keyValueFileNum(final String key) {
        int b = key.getBytes()[0] + 128;
        return b / MAX_DIRS_FOR_TABLE % MAX_FILES_FOR_DIR;
    }

    private void initTableHashMap() {
        tableHashMap = new ArrayList<>(MAX_DIRS_FOR_TABLE);
        for (int i = 0; i < MAX_DIRS_FOR_TABLE; ++i) {
            tableHashMap.add(new ArrayList<Map<String, String>>(MAX_FILES_FOR_DIR));
            for (int j = 0; j < MAX_FILES_FOR_DIR; ++j) {
                tableHashMap.get(i).add(new HashMap<String, String>());
            }
        }
    }
}
