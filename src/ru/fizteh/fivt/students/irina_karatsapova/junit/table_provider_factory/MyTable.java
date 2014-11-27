package ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTable implements Table{
    File tablePath;
    Map<String, String> committedMap = new HashMap<String, String>();
    Map<String, String> currentMap = new HashMap<String, String>();
    List<String>[][] currentKeys = new ArrayList[16][16];
    List<String>[][] committedKeys = new ArrayList[16][16];
    int changesNumber = 0;
    boolean loaded;

    MyTable(String name) throws TableException {
        tablePath = Paths.get(name).toFile();
        initKeysArray();
        LoadTable.start(this);
        copyMaps(currentMap, committedMap);
        copyKeysLists(currentKeys, committedKeys);
    }

    public String getName() {
        return tablePath.getName();
    }

    public String get(String key) {
        Utils.checkNotNull(key);
        if (currentMap.containsKey(key)) {
            return currentMap.get(key);
        } else {
            return null;
        }
    }

    public String put(String key, String value) {
        Utils.checkNotNull(key);
        Utils.checkNotNull(value);
        String oldValue = get(key);
        if (oldValue == null) {
            addKey(key);
        }
        changesNumber++;
        currentMap.put(key, value);
        return oldValue;
    }

    public String remove(String key) {
        Utils.checkNotNull(key);
        String value = get(key);
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

    public int commit() throws Exception {
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

    public int changesNumber() {
        return changesNumber;
    }

    private void initKeysArray() {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                currentKeys[dir][file] = new ArrayList<String>();
                committedKeys[dir][file] = new ArrayList<String>();
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

    static void copyMaps(Map<String, String> from, Map<String, String> to) {
        to.clear();
        to.putAll(from);
    }
}
