package parallel.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import storeable.structured.Storeable;

public class Diff {
    Map<String, Storeable> creations;
    Set<String> deletions;
    Map<String, Storeable> overwrites;
    ReentrantReadWriteLock lock;
    Map<String, Storeable> data;
    MyTable table;

    public Diff(MyTable passedTable, Map<String, Storeable> passedData, 
                                                ReentrantReadWriteLock passedLock) {
        table = passedTable;
        data = passedData;
        lock = passedLock;
        creations = new HashMap<String, Storeable>();
        deletions = new HashSet<String>();
        overwrites = new HashMap<String, Storeable>();
    }

    public void put(String key, Storeable value) {
        if  (creations.containsKey(key)) {
            creations.put(key, value);
        } else if (overwrites.containsKey(key)) {
            overwrites.put(key, value);
        } else if (deletions.contains(key)) {
            deletions.remove(key);
            overwrites.put(key, value);
        } else {
            lock.readLock().lock();
            try {
                if (data.get(key) == null) {
                    creations.put(key, value);
                } else {
                    overwrites.put(key, value);
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public void remove(String key) {
        if (creations.containsKey(key)) {
            creations.remove(key);
        } else if (overwrites.containsKey(key)) {
            overwrites.remove(key);
        } else if (deletions.contains(key)) {
            return;
        } else {
            lock.readLock().lock();
            try {
                if (data.get(key) != null) {
                    deletions.add(key);
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public Storeable get(String key) {
        if (creations.containsKey(key)) {
            return creations.get(key);
        } else if (overwrites.containsKey(key)) {
            return overwrites.get(key);
        } else if (deletions.contains(key)) {
            return null;
        } else {
            lock.readLock().lock();
            try {
                return data.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public void commit() throws IOException {
        lock.writeLock().lock();
        try {
            Map<String, Storeable> oldData = 
                            table.deserializeMap(FolderData.loadDb(table.getTableRoot()));
            for (String str : deletions) {
                oldData.remove(str);
            }
            for (Map.Entry<String, Storeable> entry : creations.entrySet()) {
                oldData.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Storeable> entry : overwrites.entrySet()) {
                oldData.put(entry.getKey(), entry.getValue());
            }
            FolderData.saveDb(table.serializeMap(oldData), table.getTableRoot());
            data = oldData;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        creations.clear();
        overwrites.clear();
        deletions.clear();
    }

    public int size() {
        lock.readLock().lock();
        try {
            return data.size() + creations.size() - deletions.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int diff() {
        return creations.size() + overwrites.size() + deletions.size();
    }

    public List<String> list() {
        lock.readLock().lock();
        try {
            List<String> keyList = new ArrayList<String>();
            for (String key : data.keySet()) {
                keyList.add(key);
            }
            keyList.addAll(creations.keySet());
            keyList.removeAll(deletions);
            return keyList;
        } finally {
            lock.readLock().unlock();
        }
    }
}
