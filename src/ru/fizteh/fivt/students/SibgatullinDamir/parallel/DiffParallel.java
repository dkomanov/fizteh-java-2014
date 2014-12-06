package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Lenovo on 02.12.2014.
 */
public class DiffParallel {
    private HashMap<String, String> created;
    private HashMap<String, String> overwritten;
    private HashSet<String> deleted;
    MyTable table;
    ReentrantReadWriteLock lock;

    public DiffParallel(MyTable passedTable, ReentrantReadWriteLock passedLock) {
        created = new HashMap<>();
        deleted = new HashSet<>();
        overwritten = new HashMap<>();
        table = passedTable;
        lock = passedLock;
    }

    public String put(String key, String value) {
        if (created.containsKey(key)) {
            return created.put(key, value);
        } else if (overwritten.containsKey(key)) {
            return overwritten.put(key, value);
        } else if (deleted.contains(key)) {
            deleted.remove(key);
            overwritten.put(key, value);
            return null;
        } else {

            lock.readLock().lock();
            try {
                Storeable result = table.get(key);
                if (result == null) {
                    return created.put(key, value);
                } else {
                    overwritten.put(key, value);
                    return table.getProvider().serialize(table, result);
                }
            } finally {
                lock.readLock().unlock();
            }

        }
    }

    public String remove(String key) {
        if (created.containsKey(key)) {
            return created.remove(key);
        } else if (overwritten.containsKey(key)) {
            return overwritten.remove(key);
        } else if (deleted.contains(key)) {
            return null;
        } else {

            lock.readLock().lock();
            try {
                Storeable result = table.get(key);
                if (result == null) {
                    return null;
                } else {
                    deleted.add(key);
                    return table.getProvider().serialize(table, result);
                }
            } finally {
                lock.readLock().unlock();
            }

        }
    }

    public String get(String key) {
        if (created.containsKey(key)) {
            return created.get(key);
        } else if (overwritten.containsKey(key)) {
            return overwritten.get(key);
        } else if (deleted.contains(key)) {
            return null;
        } else {

            lock.readLock().lock();
            try {
                Storeable result = table.get(key);
                if (result == null) {
                    return null;
                } else {
                    return table.getProvider().serialize(table, result);
                }
            } finally {
                lock.readLock().unlock();
            }

        }
    }

    public int deltaSize() {
        return created.size() - deleted.size();
    }

    public int changesCount() {
        return created.size() + deleted.size() + overwritten.size();
    }

    public int clear() {
        int countOfChanges = created.size() + deleted.size() + overwritten.size();
        created.clear();
        deleted.clear();
        overwritten.clear();
        return countOfChanges;
    }

    public int commit() throws IOException {

        lock.writeLock().lock();
        try {
            int countOfChanges = created.size() + deleted.size() + overwritten.size();
            for (Map.Entry<String, String> entry : created.entrySet()) {
                table.put(entry.getKey(), table.getProvider().deserialize(table, entry.getValue()));
            }
            for (Map.Entry<String, String> entry : overwritten.entrySet()) {
                table.put(entry.getKey(), table.getProvider().deserialize(table, entry.getValue()));
            }
            deleted.forEach(table::remove);
            table.commit();
            return countOfChanges;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }

    }
}
