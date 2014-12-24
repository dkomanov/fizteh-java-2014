package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.utils.TypeStringTranslator;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.utils.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class DbTable implements Table {
    private final Path tableDir;
    private final List<Class<?>> columnTypes;
    private final TableProvider tableProvider;

    private static final int MAX_DIRS_FOR_TABLE = 16;
    private static final int MAX_FILES_FOR_DIR = 16;

    private ThreadLocal<Integer> size = new ThreadLocal<Integer>() {
        @Override protected Integer initialValue() {
            return new Integer(0);
        }
    };

    private ThreadLocal<Diff> diff = new ThreadLocal<Diff>() {
        @Override protected Diff initialValue() {
            return new Diff();
        }
    };
    private ReentrantReadWriteLock tableLock = new ReentrantReadWriteLock(true);
    private List<List<Map<String, String>>> lastCommitTableMap;
    private Integer lastCommitTableMapSize;


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
            setSize(0);
            lastCommitTableMapSize = getSize();
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
            lastCommitTableMapSize = getSize();
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
        if (getDiffDeletionsSet().contains(key)) {
            return null;
        } else if (getDiffChangesMap().containsKey(key)) {
            return deserializeWrapper(getDiffChangesMap().get(key));
        } else {
            tableLock.readLock().lock();
            try {
                return deserializeWrapper(getTablePartByKey(key).get(key));
            } finally {
                tableLock.readLock().unlock();
            }
        }
    }

    @Override
    public Storeable put(final String key, final Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        checkStoreableValueValidity(value);
        if (getDiffChangesMap().containsKey(key)) {   // Was changed/added in current commit.
            String uncommitedValue = getDiffChangesMap().get(key);
            getDiffChangesMap().put(key, serializeWrapper(value));
            return deserializeWrapper(uncommitedValue);
        } else if (getDiffDeletionsSet().contains(key)) { // Was deleted in current commit.
            getDiffDeletionsSet().remove(key);
            getDiffChangesMap().put(key, serializeWrapper(value));
            incSize();
            return null;
        } else {    // It hasn't been deleted or changed yet. We change/add this key-value pair now.
            Storeable oldValue;
            tableLock.readLock().lock();
            try {
                oldValue = deserializeWrapper(getTablePartByKey(key).get(key));
            } finally {
                tableLock.readLock().unlock();
            }
            if (oldValue != null) { // Changing.
                getDiffChangesMap().put(key, serializeWrapper(value));
                return oldValue;
            } else {    // Adding.
                getDiffChangesMap().put(key, serializeWrapper(value));
                incSize();
                return null;
            }
        }
    }

    @Override
    public List<String> list() {
        // Append old/changed keys (and not deleted) to list.
        List<String> keysList = new LinkedList<>();
        tableLock.readLock().lock();
        try {
            for (List<Map<String, String>> list : lastCommitTableMap) {
                for (Map<String, String> map : list) {
                    Set<String> keySet = map.keySet();
                    for (String key : keySet) {
                        if (!getDiffDeletionsSet().contains(key)) {
                            keysList.add(key);
                        }
                    }
                }
            }

            // Append NEW keys to list.
            for (String key : getDiffChangesMap().keySet()) {
                if (!lastCommitTableMap.contains(key)) {
                    keysList.add(key);
                }
            }
            return keysList;
        } finally {
            tableLock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable prevCommitValue;
        tableLock.readLock().lock();
        try {
            prevCommitValue = deserializeWrapper(getTablePartByKey(key).get(key));
            // This pair was deleted in this commit or hasn't been
            // changed in this commit and was absent in previous commit.
            if ((prevCommitValue == null && !getDiffChangesMap().containsKey(key))
                    || getDiffDeletionsSet().contains(key)) {
                return null;
            } else {
                getDiffDeletionsSet().add(key);
                decSize();
                if (!getDiffChangesMap().containsKey(key)) {   // Then prevCommitValue != null.
                    return prevCommitValue;
                } else {
                    Storeable oldValue = deserializeWrapper(getDiffChangesMap().get(key));
                    getDiffChangesMap().remove(key);
                    return oldValue;
                }
            }
        } finally {
            tableLock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        if (getDiffDeletionsSet().size() + getDiffChangesMap().size() == 0) {
            return 0;
        }
        tableLock.writeLock().lock();
        try {
            commitChangesToTableMap();
            dump();
            int res = getDiffChangesMap().size() + getDiffDeletionsSet().size();
            getDiffChangesMap().clear();
            getDiffDeletionsSet().clear();
            lastCommitTableMapSize = getSize();
            return res;
        } catch (IOException e) {
            throw new RuntimeException("can't dump table on commit: " + e.getMessage());
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        if (getDiffDeletionsSet().size() + getDiffChangesMap().size() == 0) {
            return 0;
        }
        int cancelledChanges = getDiffChangesMap().size() + getDiffDeletionsSet().size();
        getDiffDeletionsSet().clear();
        getDiffChangesMap().clear();
        tableLock.readLock().lock();
        try {
            setSize(lastCommitTableMapSize);
        } finally {
            tableLock.readLock().unlock();
        }
        return cancelledChanges;
    }


    @Override
    public String getName() {
        return Utility.getNameByPath(tableDir);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return getDiffChangesMap().size() + getDiffDeletionsSet().size();
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
        for (Map.Entry<String, String> pair : getDiffChangesMap().entrySet()) {
            getTablePartByKey(pair.getKey()).put(pair.getKey(), pair.getValue());
        }
        for (String deletedKey : getDiffDeletionsSet()) {
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
                incSize(map.size());
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
        lastCommitTableMap = new ArrayList<>(MAX_DIRS_FOR_TABLE);
        for (int i = 0; i < MAX_DIRS_FOR_TABLE; ++i) {
            lastCommitTableMap.add(new ArrayList<>(MAX_FILES_FOR_DIR));
            for (int j = 0; j < MAX_FILES_FOR_DIR; ++j) {
                lastCommitTableMap.get(i).add(new HashMap<String, String>());
            }
        }
    }

    private Map<String, String> getDiffChangesMap() {
        return diff.get().changesMap;
    }

    private Set<String> getDiffDeletionsSet() {
        return diff.get().deletionsSet;
    }

    private Integer getSize() {
        return size.get();
    }

    private void setSize(Integer value) {
        size.set(value);
    }

    private void incSize() {
        setSize(getSize() + 1);
    }

    private void incSize(Integer incBy) {
        setSize(getSize() + incBy);
    }

    private void decSize() {
        setSize(getSize() - 1);
    }

}
