package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by user1 on 17.11.2014.
 */
public class DataBaseTableDiff {
    Map<String, Storeable> addMap;
    Map<String, Storeable> deleteMap;
    Map<String, Storeable> rewriteMap;
    DataBaseTable table;
    ReentrantReadWriteLock lock;
    int localVersion = 0;

    public DataBaseTableDiff(DataBaseTable table, ReentrantReadWriteLock lock) {
        this.table = table;
        this.lock = lock;
        addMap = new HashMap<>();
        deleteMap = new HashMap<>();
        rewriteMap = new HashMap<>();
    }

    private void fixVersion() {
        lock.readLock().lock();
        try {
            if (table.getVersion() > localVersion) {
                localVersion = table.getVersion();
            } else {
                return;
            }
            ArrayList<String> toRemove = new ArrayList<>();
            for (Map.Entry<String, Storeable> entry : deleteMap.entrySet()) {
                if (!table.tempData.containsKey(entry.getKey())) {
                    toRemove.add(entry.getKey());
                }
            }
            for (String removeKey : toRemove) {
                deleteMap.remove(removeKey);
            }
            HashMap<String, Storeable> toAdd = new HashMap<>();
            for (Map.Entry<String, Storeable> entry : rewriteMap.entrySet()) {
                if (!table.tempData.containsKey(entry.getKey())) {
                    toAdd.put(entry.getKey(), entry.getValue());
                }
            }
            HashMap<String, Storeable> toRewrite = new HashMap<>();
            for (Map.Entry<String, Storeable> entry : addMap.entrySet()) {
                if (table.tempData.containsKey(entry.getKey())) {
                    toRewrite.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Storeable> entry : toAdd.entrySet()) {
                rewriteMap.remove(entry.getKey());
                addMap.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Storeable> entry : toRewrite.entrySet()) {
                Storeable result = addMap.remove(entry.getKey());
                if (!result.equals(entry.getValue())) {
                    rewriteMap.put(entry.getKey(), entry.getValue());
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public Storeable put(String key, Storeable value) {
        fixVersion();
        if (addMap.containsKey(key)) {
            Storeable result = addMap.get(key);
            addMap.put(key, value);
            return result;
        }
        if (rewriteMap.containsKey(key)) {
            Storeable result = addMap.get(key);
            rewriteMap.put(key, value);
            return result;
        }
        if (deleteMap.containsKey(key)) {
            deleteMap.remove(key);
            rewriteMap.put(key, value);
            return null;
        }

        lock.readLock().tryLock();
        try {
            Storeable result = table.originGet(key);
            if (result == null) {
                addMap.put(key, value);
                return null;
            } else {
                rewriteMap.put(key, value);
                return result;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public Storeable get(String key) {
        fixVersion();
        if (addMap.containsKey(key)) {
            return addMap.get(key);
        }
        if (rewriteMap.containsKey(key)) {
            return rewriteMap.get(key);
        }
        lock.readLock().lock();
        try {
            return table.originGet(key);
        } finally {
            lock.readLock().unlock();
        }
    }


    public int changesSize() {
        fixVersion();
        return addMap.size() + deleteMap.size() + rewriteMap.size();
    }

    public Storeable remove(String key) {
        fixVersion();
        if (addMap.containsKey(key)) {
            return addMap.remove(key);
        }
        if (rewriteMap.containsKey(key)) {
            return rewriteMap.remove(key);
        }
        if (deleteMap.containsKey(key)) {
            return null;
        }
        try {
            lock.readLock().lock();
            Storeable result = table.originGet(key);
            if (result != null) {
                deleteMap.put(key, result);
                return result;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void commit() throws IOException {
        fixVersion();
        lock.writeLock().lock();
        try {
            localVersion++;
            table.version = localVersion;
            for (String key : deleteMap.keySet()) {
                table.originRemove(key);
            }
            for (Map.Entry<String, Storeable> entry : addMap.entrySet()) {
                table.originPut(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Storeable> entry : rewriteMap.entrySet()) {
                table.originPut(entry.getKey(), entry.getValue());
            }
            table.saveMap();
        } catch (Exception e) {
            throw new IOException("input/output error writing database");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearDiff() {
        rewriteMap.clear();
        addMap.clear();
        deleteMap.clear();
    }
}
