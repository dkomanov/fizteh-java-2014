package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
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

    public DataBaseTableDiff(DataBaseTable table, ReentrantReadWriteLock lock) {
        this.table = table;
        this.lock = lock;
        addMap = new HashMap<>();
        deleteMap = new HashMap<>();
        rewriteMap = new HashMap<>();
    }


    public Storeable put(String key, Storeable value) {
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
        return addMap.size() + deleteMap.size() + rewriteMap.size();
    }

    public Storeable remove(String key) {
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
        lock.writeLock().lock();
        try {
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
