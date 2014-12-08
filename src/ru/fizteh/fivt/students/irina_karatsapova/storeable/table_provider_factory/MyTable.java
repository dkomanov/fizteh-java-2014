package ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTable implements Table {
    File tablePath;
    Map<String, Storeable> committedMap = new HashMap<>();
    Map<String, Storeable> currentMap = new HashMap<>();
    List<String>[][] currentKeys = new ArrayList[16][16];
    List<String>[][] committedKeys = new ArrayList[16][16];
    int changesNumber = 0;
    boolean loaded;
    TableProvider tableProvider;
    List<Class<?>> columnsType = new ArrayList<>();

    MyTable(String name, TableProvider tableProvider) throws TableException {
        tablePath = Paths.get(name).toFile();
        this.tableProvider = tableProvider;
        initKeysArray();
        LoadTable.start(this);
        copyMaps(currentMap, committedMap);
        copyKeysLists(currentKeys, committedKeys);
    }

    public String getName() {
        return tablePath.getName();
    }

    public Storeable get(String key) {
        Utils.checkNotNull(key);
        if (currentMap.containsKey(key)) {
            return currentMap.get(key);
        } else {
            return null;
        }
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException {
        Utils.checkNotNull(key);
        Utils.checkNotNull(value);
        TableUtils.checkColumnsFormat(this, value);
        Storeable oldValue = get(key);
        if (oldValue == null) {
            addKey(key);
        }
        changesNumber++;
        currentMap.put(key, value);
        return oldValue;
    }

    public Storeable remove(String key) {
        Utils.checkNotNull(key);
        Storeable value = get(key);
        if (value != null) {
            currentMap.remove(key);
            changesNumber++;
            deleteKey(key);
        }
        return value;
    }

    public int size() {
        return currentMap.size();
    }

    public List<String> list() {
        List<String> keys = new ArrayList<>();
        for (List<String>[] dirKeys: currentKeys) {
            for (List<String> fileKeys: dirKeys) {
                for (String key: fileKeys) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    public int commit() throws  TableException {
        SaveTable.start(this);
        copyMaps(currentMap, committedMap);
        copyKeysLists(currentKeys, committedKeys);
        int happenedChanges = changesNumber;
        changesNumber = 0;
        return happenedChanges;
    }

    public int rollback() {
        copyMaps(committedMap, currentMap);
        copyKeysLists(committedKeys, currentKeys);
        int notHappenedChanges = changesNumber;
        changesNumber = 0;
        return notHappenedChanges;
    }

    public int getNumberOfUncommittedChanges() {
        return changesNumber;
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

    private void initKeysArray() {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                currentKeys[dir][file] = new ArrayList<>();
                committedKeys[dir][file] = new ArrayList<>();
            }
        }
    }

    void addKey(String key) {
        byte keyHash = key.getBytes()[0];
        int dir = keyHash % 16;
        int file = keyHash / 16 % 16;
        currentKeys[dir][file].add(key);
    }

    void deleteKey(String key) {
        byte keyHash = key.getBytes()[0];
        int dir = keyHash % 16;
        int file = keyHash / 16 % 16;
        currentKeys[dir][file].remove(key);
    }

    void copyKeysLists(List<String>[][] from, List<String>[][] to) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                to[dir][file] = from[dir][file];
            }
        }
    }

    static void copyMaps(Map<String, Storeable> from, Map<String, Storeable> to) {
        to.clear();
        to.putAll(from);
    }
}
