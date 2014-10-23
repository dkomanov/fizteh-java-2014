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
    private List<List<Map<String, String>>> tableHashMap = null;

    public TableManager(final Path tablePath) throws IOException {
        this.tablePath = tablePath.toAbsolutePath().normalize();
        initTableHashMap();
        TableLoaderDumper.loadTable(this.tablePath, tableHashMap);
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
        return tableHashMap.get(dir).get(file).put(key, value);
    }

    public List<String> list() {
        List<String> keysList = new LinkedList<>();
        for (List<Map<String, String>> list : tableHashMap) {
            for (Map<String, String> map : list) {
                Set<String> keySet = map.keySet();
                Iterator<String> keySetIter = keySet.iterator();
                while (keySetIter.hasNext()) {
                    keysList.add(keySetIter.next());
                }
            }
        }
        return keysList;
    }

    public String remove(final String key) {
        int dir = keyValueDirNum(key);
        int file = keyValueFileNum(key);
        return tableHashMap.get(dir).get(file).get(key);
    }

    public String getTableName() {
        return tablePath.getFileName().toString();
    }

    private static int keyValueDirNum(final String key) {
        byte b = key.getBytes()[0];
        return b % MAX_DIRS_FOR_TABLE;
    }

    private static int keyValueFileNum(final String key) {
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
