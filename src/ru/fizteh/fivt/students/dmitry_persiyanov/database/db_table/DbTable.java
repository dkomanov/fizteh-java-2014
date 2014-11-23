package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.TypeStringTranslator;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

public final class DbTable implements Table {
    private final Path tableDir;
    private final List<Class<?>> columnTypes;
    private final TableProvider tableProvider;

    private static final int MAX_DIRS_FOR_TABLE = 16;
    private static final int MAX_FILES_FOR_DIR = 16;

    private int size;
    private List<List<Map<String, String>>> lastCommitTableMap;
    private int lastCommitTableMapSize;
    // Important invariant: uncommittedChangesMap doesn't intersect uncommittedDeletionsSet!
    private Map<String, String> uncommittedChangesMap;
    private Set<String> uncommittedDeletionsSet;

    /**
     * Creates empty table. tableDir must be empty directory.
     * @param tableDir
     * @param columnTypes
     * @param tableProvider
     * @return Empty table.
     */
    public static DbTable createDbTable(final Path tableDir,
                                        final List<Class<?>> columnTypes,
                                        final TableProvider tableProvider) {
        return new DbTable(tableDir, columnTypes, tableProvider);
    }

    public static DbTable loadExistingDbTable(final Path tableDir, final TableProvider tableProvider) {
        return new DbTable(tableDir, tableProvider);
    }

    // This ctor CREATES unexisted table.
    private DbTable(final Path tableDir, final List<Class<?>> columnTypes, final TableProvider tableProvider) {
        if (!Files.isDirectory(tableDir)) {
            throw new IllegalArgumentException("is not a directory: " + tableDir.toString());
        } else {
            this.tableProvider = tableProvider;
            this.columnTypes = new ArrayList<>();
            this.columnTypes.addAll(columnTypes);
            this.tableDir = tableDir;
            initHashMaps();
            try {
                TableLoaderDumper.createTable(this.tableDir, columnTypes);
            } catch (IOException e) {
                throw new RuntimeException("can't create table from \'" + tableDir.toString() + "\'"
                        + ", [" + e.getMessage() + "]");
            }
            size = 0;
            lastCommitTableMapSize = size;
        }
    }

    // This ctor LOADS existed table.
    private DbTable(final Path tableDir, final TableProvider tableProvider) {
        if (!Files.isDirectory(tableDir)) {
            throw new IllegalArgumentException("is not a directory: " + tableDir.toString());
        } else {
            this.tableProvider = tableProvider;
            this.columnTypes = new ArrayList<>();
            this.tableDir = tableDir;
            initHashMaps();
            try {
                TableLoaderDumper.loadTable(this.tableDir, lastCommitTableMap, columnTypes);
            } catch (IOException e) {
                throw new RuntimeException("can't load table from \'" + tableDir.toString() + "\'"
                        + ", [" + e.getMessage() + "]");
            }
            calculateTableSize();
            lastCommitTableMapSize = size;
        }
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException();
        } else {
            return columnTypes.get(columnIndex);
        }
    }

    @Override
    public Storeable get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (uncommittedDeletionsSet.contains(key)) {
            return null;
        } else if (uncommittedChangesMap.containsKey(key)) {
            return deserializeWrapper(uncommittedChangesMap.get(key));
        } else {
            return deserializeWrapper(getTablePartByKey(key).get(key));
        }
    }

    @Override
    public Storeable put(final String key, final Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        checkStoreableValueValidity(value);
        if (uncommittedChangesMap.containsKey(key)) {   // Was changed/added in current commit.
            String uncommitedValue = uncommittedChangesMap.get(key);
            uncommittedChangesMap.put(key, serializeWrapper(value));
            return deserializeWrapper(uncommitedValue);
        } else if (uncommittedDeletionsSet.contains(key)) { // Was deleted in current commit.
            uncommittedDeletionsSet.remove(key);
            uncommittedChangesMap.put(key, serializeWrapper(value));
            size++;
            return null;
        } else {    // It hasn't been deleted or changed yet. We change/add this key-value pair now.
            Storeable oldValue = deserializeWrapper(getTablePartByKey(key).get(key));
            if (oldValue != null) { // Changing.
                uncommittedChangesMap.put(key, serializeWrapper(value));
                return oldValue;
            } else {    // Adding.
                uncommittedChangesMap.put(key, serializeWrapper(value));
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
    public Storeable remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable prevCommitValue = deserializeWrapper(getTablePartByKey(key).get(key));
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
                Storeable oldValue = deserializeWrapper(uncommittedChangesMap.get(key));
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
        return Utility.getNameByPath(tableDir);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return uncommittedChangesMap.size() + uncommittedDeletionsSet.size();
    }

    private String serializeWrapper(final Storeable value) {
        return tableProvider.serialize(this, value);
    }

    private Storeable deserializeWrapper(final String value) {
        if (value == null) {
            return null;
        } else {
            try {
                return tableProvider.deserialize(this, value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("error while deserializing value \""
                        + value + "\": " + e.getMessage());
            }
        }
    }

    private void commitChangesToTableMap() {
        for (Map.Entry<String, String> pair : uncommittedChangesMap.entrySet()) {
            getTablePartByKey(pair.getKey()).put(pair.getKey(), pair.getValue());
        }
        for (String deletedKey : uncommittedDeletionsSet) {
            lastCommitTableMap.remove(deletedKey);
        }
    }

    private Map<String, String> getTablePartByKey(final String key) {
        int dir = getDirNumByKey(key);
        int file = getFileNumByKey(key);
        return lastCommitTableMap.get(dir).get(file);
    }

    private void calculateTableSize() {
        for (List<Map<String, String>> list : lastCommitTableMap) {
            for (Map<String, String> map : list) {
                size += map.size();
            }
        }
    }

    private boolean checkStoreableValueValidity(final Storeable value) {
        for (int i = 0; i < columnTypes.size(); ++i) {
            if (value.getColumnAt(i) != null && !columnTypes.get(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("types incompatibility: column index " + i
                        + ", table type: " + TypeStringTranslator.getStringNameByType(columnTypes.get(i))
                        + ", passed type: "
                        + TypeStringTranslator.getStringNameByType(value.getColumnAt(i).getClass()));
            }
        }
        return true;
    }

    private void dump() throws IOException {
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
            lastCommitTableMap.add(new ArrayList<>(MAX_FILES_FOR_DIR));
            for (int j = 0; j < MAX_FILES_FOR_DIR; ++j) {
                lastCommitTableMap.get(i).add(new HashMap<String, String>());
            }
        }
    }
}
