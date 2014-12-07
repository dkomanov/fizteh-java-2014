package ru.fizteh.fivt.students.irina_karatsapova.proxy.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table, AutoCloseable {
    boolean loaded = false;
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
            copyMaps(committedMap, newDiff.currentMap);
            copyKeysLists(committedKeys, newDiff.currentKeys);
            tableAccessLock.readLock().unlock();
            return newDiff;
        }
    };

    MyTable(MyTable table) {
        tablePath = table.tablePath;
        tableProvider = table.tableProvider;
        columnsType = table.columnsType;
        committedKeys = table.committedKeys;
        committedMap = table.committedMap;
        table.loaded = true;
    }

    MyTable(String name, TableProvider tableProvider) throws ThreadInterruptException {
        loaded = true;
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
        checkLoaded();
        Utils.checkNotNull(key);
        Storeable lookedForValue = null;
        if (currentMap().containsKey(key)) {
            lookedForValue = currentMap().get(key);
        }
        return lookedForValue;
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkLoaded();
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
        checkLoaded();
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
        checkLoaded();
        return currentMap().size();
    }

    public List<String> list() {
        checkLoaded();
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
        checkLoaded();
        tableAccessLock.writeLock().lock();
        SaveTable.start(this);
        copyMaps(currentMap(), committedMap);
        copyKeysLists(currentKeys(), committedKeys);
        tableAccessLock.writeLock().unlock();
        int happenedChanges = changesNumber();
        diff.get().changesNumber = 0;
        return happenedChanges;
    }

    public int rollback() {
        checkLoaded();
        int notHappenedChanges = changesNumber();
        renewDiff();
        return notHappenedChanges;
    }

    public int getNumberOfUncommittedChanges() {
        checkLoaded();
        return changesNumber();
    }

    public int getColumnsCount() {
        checkLoaded();
        return columnsType.size();
    }

    public Class<?> getColumnType(int index) throws ColumnFormatException {
        checkLoaded();
        if (index >= getColumnsCount()) {
            throw new ColumnFormatException("no column with index " + index
                                              + " (number of columns is " + getColumnsCount() + ")");
        }
        return columnsType.get(index);
    }

    public String toString() {
        checkLoaded();
        return getClass().getSimpleName() + "[" + tablePath + "]";
    }

    public void close() {
        checkLoaded();
        rollback();
        loaded = false;
    }


    private void initKeysArray(List<String>[][] keys) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
//                currentKeys()[dir][file] = new ArrayList<>();
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
        copyMaps(committedMap, newDiff.currentMap);
        copyKeysLists(committedKeys, newDiff.currentKeys);
        tableAccessLock.readLock().unlock();
        diff.set(newDiff);
    }



    private void checkLoaded() throws IllegalStateException {
        if (!loaded) {
            throw new IllegalStateException("Table is not loaded");
        }
    }
}
