package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiThreadManager {
    public MultiThreadManager(StoreableTable givenTable, ReentrantReadWriteLock givenLock) {
        table = givenTable;
        lock = givenLock;
        deletions = new HashSet<>();
        creations = new HashMap<String, String>();
        overwrites = new HashMap<String, String>();
    }

    private StoreableTable table;
    private ReentrantReadWriteLock lock;
    private Set<String> deletions;
    private Map<String, String> creations;
    private Map<String, String> overwrites;

    public String put(String key, String value) {
        if (creations.containsKey(key)) {
            return creations.put(key, value);
        } else if (overwrites.containsKey(key)) {
            return overwrites.put(key, value);
        } else if (deletions.contains(key)) {
            deletions.remove(key);
            overwrites.put(key, value);
            return null;
        } else {
            lock.readLock().lock();
            try {
                Storeable oldValue = table.get(key);
                if (oldValue == null) {
                    creations.put(key, value);
                    return null;
                } else {
                    overwrites.put(key, value);
                    return table.getProvider().serialize(table, oldValue);
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public String remove(String key) {
        if (creations.containsKey(key)) {
            return creations.remove(key);
        } else if (overwrites.containsKey(key)) {
            return overwrites.remove(key);
        } else if (deletions.contains(key)) {
            return null;
        } else {
            lock.readLock().lock();
            try {
                Storeable oldValue = table.get(key);
                if (oldValue != null) {
                    deletions.add(key);
                    return table.getProvider().serialize(table, oldValue);
                } else {
                    return null;
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public String get(String key) {
        if (creations.containsKey(key)) {
            return creations.get(key);
        } else if (overwrites.containsKey(key)) {
            return overwrites.get(key);
        } else if (deletions.contains(key)) {
            return null;
        } else {
            lock.readLock().lock();
            try {
                Storeable oldValue = table.get(key);
                if (oldValue == null) {
                    return null;
                } else {
                    return table.getProvider().serialize(table, oldValue);
                }
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public void commit() throws IOException {
        lock.writeLock().lock();
        try {
            deletions.forEach(table::remove);
            for (HashMap.Entry<String, String> entry : creations.entrySet()) {
                table.put(entry.getKey(), table.getProvider().deserialize(table, entry.getValue()));
            }
            for (HashMap.Entry<String, String> entry : overwrites.entrySet()) {
                table.put(entry.getKey(), table.getProvider().deserialize(table, entry.getValue()));
            }
            table.commit();
        } catch (ParseException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        creations.clear();
        overwrites.clear();
        deletions.clear();
    }

    public Set<String> getCreations() {
        return creations.keySet();
    }

    public Set<String> getDeletions() {
        return deletions;
    }

    public int changesCount() {
        return creations.size() + overwrites.size() + deletions.size();
    }

    public int diffCount() {
        return creations.size() - deletions.size();
    }
}
