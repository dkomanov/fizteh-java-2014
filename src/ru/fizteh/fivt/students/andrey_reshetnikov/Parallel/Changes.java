package ru.fizteh.fivt.students.andrey_reshetnikov.Parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Changes {
    //изменения покрывающие все действия потока над таблицей
    private final Map<String, String> overwriting;
    private final Map<String, String> creating;
    private final Set<String> deleting;
    private final MyStoreableTable origin;
    private final ReentrantReadWriteLock myLock;

    public Changes(MyStoreableTable table, ReentrantReadWriteLock lock) {
        origin = table;
        myLock = lock;
        overwriting = new HashMap<>();
        creating = new HashMap<>();
        deleting = new HashSet<>();
    }

    public String get(String key) {
        if (creating.containsKey(key)) {
            return creating.get(key);
        } else if (overwriting.containsKey(key)) {
            return overwriting.get(key);
        } else if (deleting.contains(key)) {
            return null;
        } else {
            myLock.readLock().lock();
            try {
                Storeable originResult = origin.get(key);
                if (originResult == null) {
                    return null;
                } else {
                    return origin.getProvider().serialize(origin, originResult);
                }
            } finally {
                myLock.readLock().unlock();
            }
        }
    }

    public String put(String key, String value) {
        if (creating.containsKey(key)) {
            return creating.put(key, value);
        } else if (overwriting.containsKey(key)) {
            return overwriting.put(key, value);
        } else if (deleting.contains(key)) {
            deleting.remove(key);
            overwriting.put(key, value);
            return null;
        } else {
            myLock.readLock().lock();
            try {
                Storeable originResult = origin.get(key);
                if (originResult == null) {
                    creating.put(key, value);
                    return null;
                } else {
                    overwriting.put(key, value);
                    return origin.getProvider().serialize(origin, originResult);
                }
            } finally {
                myLock.readLock().unlock();
            }
        }
    }

    public String remove(String key) {
        if (creating.containsKey(key)) {
            return creating.remove(key);
        } else if (overwriting.containsKey(key)) {
            return overwriting.remove(key);
        } else if (deleting.contains(key)) {
            return null;
        } else {
            myLock.readLock().lock();
            try {
                Storeable originResult = origin.get(key);
                if (originResult != null) {
                    deleting.add(key);
                    return origin.getProvider().serialize(origin, originResult);
                } else {
                    return null;
                }
            } finally {
                myLock.readLock().lock();
            }
        }
    }

    public int deltaSize() {
        return creating.size() - deleting.size();
    }

    public int differencesCount() {
        return creating.size() + deleting.size() + overwriting.size();
    }

    public void commit() throws IOException {
        myLock.readLock().lock();
        try {
            try {
                for (String delete : deleting) {
                    origin.remove(delete);
                }
                for (Map.Entry<String, String> entry : creating.entrySet()) {
                    origin.put(entry.getKey(), origin.getProvider().deserialize(origin, entry.getValue()));
                }
                for (Map.Entry<String, String> entry : overwriting.entrySet()) {
                    origin.put(entry.getKey(), origin.getProvider().deserialize(origin, entry.getValue()));
                }
                origin.commit();
            } catch (ParseException e) {
                throw new IOException(e.getMessage());
            } finally {
                myLock.readLock().lock();
            }
        } finally {
            myLock.readLock().lock();
        }
    }

    public void clear() {
        creating.clear();
        deleting.clear();
        overwriting.clear();
    }

    public Set<String> getCreated() {
        return creating.keySet();
    }

    public Set<String> getRemoved() {
        return deleting;
    }
}
