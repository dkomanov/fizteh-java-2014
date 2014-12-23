package ru.fizteh.fivt.students.irina_karatsapova.parallel.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table {
    File tablePath;
    TableProvider tableProvider;
    List<Class<?>> columnsType = new ArrayList<>();
    Map<String, Storeable> committedMap = new HashMap<>();
    List<String>[][] committedKeys = new ArrayList[16][16];
    private ReentrantReadWriteLock tableAccessLock = new ReentrantReadWriteLock(true);

    private class Diff {
        List<String>[][] currentKeys = new ArrayList[16][16];
        Map<String, Storeable> currentMap = new HashMap<>();
        Integer changesNumber = 0;

        Diff() {
            initKeysArray(currentKeys);
        }
    }

    private ThreadLocal<Diff> diff = new ThreadLocal<Diff>() {
        @Override
        protected Diff initialValue() {
            Diff newDiff = new Diff();
            tableAccessLock.readLock().lock();
            try {
                copyMaps(committedMap, newDiff.currentMap);
                copyKeysLists(committedKeys, newDiff.currentKeys);
            } finally {
                tableAccessLock.readLock().unlock();
            }
            return newDiff;
        }
    };

    MyTable(String name, TableProvider tableProvider) throws ThreadInterruptException {
        tablePath = Paths.get(name).toFile();
        this.tableProvider = tableProvider;
        initKeysArray(committedKeys);
        LoadTable.start(this);
        copyMaps(currentMap(), committedMap);
        copyKeysLists(currentKeys(), committedKeys);
    }

    public String getName() {
        return tablePath.getName();
    }

    public Storeable get(String key) {
        Utils.checkNotNull(key);
        Storeable lookedForValue = null;
        if (currentMap().containsKey(key)) {
            lookedForValue = currentMap().get(key);
        }
        return lookedForValue;
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException {
        Utils.checkNotNull(key);
        Utils.checkNotNull(value);
        TableUtils.checkColumnsFormat(this, value);
        Storeable oldValue = get(key);
        if (oldValue == null) {
            addKey(key);
        }
        diff.get().changesNumber++;
        currentMap().put(key, value);
        return oldValue;
    }

    public Storeable remove(String key) {
        Utils.checkNotNull(key);
        Storeable value = get(key);
        if (value != null) {
            currentMap().remove(key);
            diff.get().changesNumber++;
            deleteKey(key);
        }
        return value;
    }

    public int size() {
        return currentMap().size();
    }

    public List<String> list() {
        List<String> keys = new ArrayList<>();
        for (List<String>[] dirKeys: currentKeys()) {
            for (List<String> fileKeys: dirKeys) {
                for (String key: fileKeys) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    public int commit() throws ThreadInterruptException {
        tableAccessLock.writeLock().lock();
        try {
            SaveTable.start(this);
            copyMaps(currentMap(), committedMap);
            copyKeysLists(currentKeys(), committedKeys);
        } finally {
            tableAccessLock.writeLock().unlock();
        }
        int happenedChanges = changesNumber();
        diff.get().changesNumber = 0;
        return happenedChanges;
    }

    public int rollback() {
        int notHappenedChanges = changesNumber();
        renewDiff();
        return notHappenedChanges;
    }

    public int getNumberOfUncommittedChanges() {
        return changesNumber();
    }

    public int getColumnsCount() {
        return columnsType.size();
    }

    public Class<?> getColumnType(int index) throws ColumnFormatException {
        if (index >= getColumnsCount()) {
            throw new ColumnFormatException("no column with index " + index
                                              + " (number of columns is " + getColumnsCount() + ")");
        }
        return columnsType.get(index);
    }

    private void initKeysArray(List<String>[][] keys) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                keys[dir][file] = new ArrayList<>();
            }
        }
    }

    void addKey(String key) {
        byte keyHash = key.getBytes()[0];
        int dir = keyHash % 16;
        int file = keyHash / 16 % 16;
        currentKeys()[dir][file].add(key);
    }

    void deleteKey(String key) {
        byte keyHash = key.getBytes()[0];
        int dir = keyHash % 16;
        int file = keyHash / 16 % 16;
        currentKeys()[dir][file].remove(key);
    }

    void copyKeysLists(List<String>[][] from, List<String>[][] to) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                to[dir][file].clear();
                for (String key: from[dir][file]) {
                    to[dir][file].add(key);
                }
            }
        }
    }

    static void copyMaps(Map<String, Storeable> from, Map<String, Storeable> to) {
        to.clear();
        to.putAll(from);
    }

    Map<String, Storeable> currentMap() {
        return diff.get().currentMap;
    }

    List<String>[][] currentKeys() {
        return diff.get().currentKeys;
    }

    Integer changesNumber() {
        return diff.get().changesNumber;
    }

    void renewDiff() {
        Diff newDiff = new Diff();
        tableAccessLock.readLock().lock();
        try {
            copyMaps(committedMap, newDiff.currentMap);
            copyKeysLists(committedKeys, newDiff.currentKeys);
        } finally {
            tableAccessLock.readLock().unlock();
        }
        diff.set(newDiff);
    }
}
