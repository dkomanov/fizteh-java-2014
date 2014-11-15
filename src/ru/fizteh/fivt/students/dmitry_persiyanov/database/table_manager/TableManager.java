package ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TableManager implements Table {
    public final File tableDir;
    public static final int MAX_DIRS_FOR_TABLE = 16;
    public static final int MAX_FILES_FOR_DIR = 16;
    private int size;
    private List<List<Map<String, String>>> lastCommitTableMap;
    private int lastCommitTableMapSize;
    // Important invariant: uncommittedChangesMap doesn't intersect uncommittedDeletionsSet!
    private Map<String, String> uncommittedChangesMap;
    private Set<String> uncommittedDeletionsSet;


    public TableManager(final File tableDir) {
        if (!tableDir.isDirectory()) {
            throw new IllegalArgumentException("is not a directory: " + tableDir.getPath());
        } else {
            this.tableDir = tableDir;
            initHashMaps();
            try {
                TableLoaderDumper.loadTable(this.tableDir, lastCommitTableMap);
            } catch (IOException e) {
                throw new RuntimeException("can't load table from \'" + tableDir.getPath() + "\'"
                        + ", [" + e.getMessage() + "]");
            }
            calculateTableSize();
            lastCommitTableMapSize = size;
        }
    }

    @Override
    public String get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (uncommittedDeletionsSet.contains(key)) {
            return null;
        } else {
            String uncommitedValue = uncommittedChangesMap.get(key);
            if (uncommitedValue == null) {
                int dir = getDirNumByKey(key);
                int file = getFileNumByKey(key);
                return lastCommitTableMap.get(dir).get(file).get(key);
            } else {
                return uncommitedValue;
            }
        }
    }

    @Override
    public String put(final String key, final String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        int dir = getDirNumByKey(key);
        int file = getFileNumByKey(key);
        if (uncommittedChangesMap.containsKey(key)) {   // Was changed/added in current commit.
            String uncommitedValue = uncommittedChangesMap.get(key);
            uncommittedChangesMap.put(key, value);
            return uncommitedValue;
        } else if (uncommittedDeletionsSet.contains(key)) { // Was deleted in current commit.
            uncommittedDeletionsSet.remove(key);
            uncommittedChangesMap.put(key, value);
            size++;
            return null;
        } else {    // It hasn't been deleted or changed yet. We change/add this key-value pair now.
            String oldValue = lastCommitTableMap.get(dir).get(file).get(key);
            if (oldValue != null) { // Changing.
                uncommittedChangesMap.put(key, value);
                return oldValue;
            } else {    // Adding.
                uncommittedChangesMap.put(key, value);
                size++;
                return null;
            }
        }
    }

    @Override
    public List<String> list() {
        // Append old/changed keys (and not deleted) to list.
        List<String> keysList = new LinkedList<>();
        for (List<Map<String, String>> list : lastCommitTableMap) {
            for (Map<String, String> map : list) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if (!uncommittedDeletionsSet.contains(key)) {
                        keysList.add(key);
                    }
                }
            }
        }

        // Append NEW keys to list.
        for (String key : uncommittedChangesMap.keySet()) {
            if (!lastCommitTableMap.contains(key)) {
                keysList.add(key);
            }
        }
        return keysList;
    }

    @Override
    public String remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int dir = getDirNumByKey(key);
        int file = getFileNumByKey(key);
        String prevCommitValue = lastCommitTableMap.get(dir).get(file).get(key);
        // This pair was deleted in this commit or hasn't been changed in this commit and was absent in previous commit.
        if ((prevCommitValue == null && !uncommittedChangesMap.containsKey(key))
                || uncommittedDeletionsSet.contains(key)) {
            return null;
        } else {
            uncommittedDeletionsSet.add(key);
            size--;
            if (!uncommittedChangesMap.containsKey(key)) {   // Then prevCommitValue != null.
                return prevCommitValue;
            } else {
                String oldValue = uncommittedChangesMap.get(key);
                uncommittedChangesMap.remove(key);
                return oldValue;
            }
        }
    }

    @Override
    public int commit() {
        if (uncommittedDeletionsSet.size() == 0 && uncommittedChangesMap.size() == 0) {
            return 0;
        }
        commitChangesToTableMap();
        try {
            dump();
        } catch (IOException e) {
            throw new RuntimeException("can't dump table on commit: " + e.getMessage());
        }
        int res = uncommittedChangesMap.size() + uncommittedDeletionsSet.size();
        uncommittedChangesMap.clear();
        uncommittedDeletionsSet.clear();
        lastCommitTableMapSize = size;
        return res;
    }

    @Override
    public int rollback() {
        if (uncommittedDeletionsSet.size() == 0 && uncommittedChangesMap.size() == 0) {
            return 0;
        }
        int cancelledChanges = uncommittedChangesMap.size() + uncommittedDeletionsSet.size();
        uncommittedDeletionsSet.clear();
        uncommittedChangesMap.clear();
        size = lastCommitTableMapSize;
        return cancelledChanges;
    }

    @Override
    public String getName() {
        return tableDir.getName();
    }

    @Override
    public int size() {
        return size;
    }

    public int uncommittedChanges() {
        return uncommittedChangesMap.size() + uncommittedDeletionsSet.size();
    }

    private void commitChangesToTableMap() {
        for (Map.Entry<String, String> pair : uncommittedChangesMap.entrySet()) {
            int dir = getDirNumByKey(pair.getKey());
            int file = getFileNumByKey(pair.getKey());
            lastCommitTableMap.get(dir).get(file).put(pair.getKey(), pair.getValue());
        }
        for (String deletedKey : uncommittedDeletionsSet) {
            lastCommitTableMap.remove(deletedKey);
        }
    }

    private void calculateTableSize() {
        for (List<Map<String, String>> list : lastCommitTableMap) {
            for (Map<String, String> map : list) {
                size += map.size();
            }
        }
    }

    public void dump() throws IOException {
        TableLoaderDumper.dumpTable(tableDir, lastCommitTableMap);
    }

    private static int getDirNumByKey(final String key) {
        char b = key.charAt(0);
        return b % MAX_DIRS_FOR_TABLE;
    }

    private static int getFileNumByKey(final String key) {
        char b = key.charAt(0);
        return b / MAX_DIRS_FOR_TABLE % MAX_FILES_FOR_DIR;
    }

    private void initHashMaps() {
        uncommittedChangesMap = new HashMap<>();
        uncommittedDeletionsSet = new HashSet<>();
        lastCommitTableMap = new ArrayList<>(MAX_DIRS_FOR_TABLE);
        for (int i = 0; i < MAX_DIRS_FOR_TABLE; ++i) {
            lastCommitTableMap.add(new ArrayList<Map<String, String>>(MAX_FILES_FOR_DIR));
            for (int j = 0; j < MAX_FILES_FOR_DIR; ++j) {
                lastCommitTableMap.get(i).add(new HashMap<String, String>());
            }
        }
    }
}
