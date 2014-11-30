package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author AlexeyZhuravlev
 */

public class Diff {
    HashMap<String, String> creations;
    HashSet<String> deletions;
    HashMap<String, String> overwrites;
    StructuredTable origin;
    ReentrantReadWriteLock lock;

    public Diff(StructuredTable passedTable, ReentrantReadWriteLock passedLock) {
        origin = passedTable;
        lock = passedLock;
        creations = new HashMap<>();
        deletions = new HashSet<>();
        overwrites = new HashMap<>();
    }

    public String put(String key, String value) {
        if  (creations.containsKey(key)) {
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
                Storeable originResult = origin.get(key);
                if (originResult == null) {
                    creations.put(key, value);
                    return null;
                } else {
                    overwrites.put(key, value);
                    return origin.getProvider().serialize(origin, originResult);
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
                Storeable originResult = origin.get(key);
                if (originResult != null) {
                    deletions.add(key);
                    return origin.getProvider().serialize(origin, originResult);
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
                Storeable originResult = origin.get(key);
                if (originResult == null) {
                    return null;
                }
                return origin.getProvider().serialize(origin, originResult);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public int deltaSize() {
        return creations.size() - deletions.size();
    }

    public int changesCount() {
        return creations.size() + deletions.size() + overwrites.size();
    }

    public void clear() {
        creations.clear();
        deletions.clear();
        overwrites.clear();
    }

    public void commit() throws IOException {
        lock.writeLock().lock();
        try {
            deletions.forEach(origin::remove);
            for (Map.Entry<String, String> entry : creations.entrySet()) {
                origin.put(entry.getKey(), origin.getProvider().deserialize(origin, entry.getValue()));
            }
            for (Map.Entry<String, String> entry : overwrites.entrySet()) {
                origin.put(entry.getKey(), origin.getProvider().deserialize(origin, entry.getValue()));
            }
            origin.commit();
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }
}
