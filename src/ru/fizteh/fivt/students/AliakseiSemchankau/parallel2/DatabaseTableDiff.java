package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Aliaksei Semchankau on 01.12.2014.
 */
public class DatabaseTableDiff {

    private DatabaseTable dTable;
    private ReentrantReadWriteLock lock;
    int localVersion = 0;

    Map<String, Storeable> toPut;
    Map<String, Storeable> toRemove;
    Map<String, Storeable> toRewrite;

    public DatabaseTableDiff(DatabaseTable tableToSet, ReentrantReadWriteLock lockToSet) {
        dTable = tableToSet;
        lock = lockToSet;
        toPut = new HashMap<>();
        toRemove = new HashMap<>();
        toRewrite = new HashMap<>();
    }

    public void update() {
        lock.readLock().lock();
        try {
            if (dTable.getVersion() > localVersion) {
                localVersion = dTable.getVersion();
            } else {
                return;
            }
            ArrayList<String> unnesessaryKeysToRemove = new ArrayList<>();
            for (Map.Entry<String, Storeable> entry : toRemove.entrySet()) {
                if (!dTable.realDBMap.containsKey(entry.getKey())) {
                    unnesessaryKeysToRemove.add(entry.getKey());
                }
            }
            for (String key : unnesessaryKeysToRemove) {
                toRemove.remove(key);
            }

            ArrayList<String> toTransitKeys = new ArrayList<>();
            for (Map.Entry<String, Storeable> entry : toRewrite.entrySet()) {
                if (!dTable.realDBMap.containsKey(entry.getKey())) {
                    toTransitKeys.add(entry.getKey());
                }
            }

            for (String key : toTransitKeys) {
                Storeable store = toRewrite.get(key);
                toRewrite.remove(key);
                toPut.put(key, store);
            }

            for (Map.Entry<String, Storeable> entry : toPut.entrySet()) {
                String key = entry.getKey();
                Storeable store = entry.getValue();
                if (dTable.realDBMap.containsKey(key)) {
                    toRewrite.put(key, store);
                }
            }

        } finally {
            lock.readLock().unlock();
        }
    }

    public Storeable put(String key, Storeable store) {
        update();
        if (toPut.containsKey(key)) {
            Storeable previous = toPut.get(key);
            toPut.put(key, store);
            return previous;
        }
        if (toRewrite.containsKey(key)) {
            Storeable previous = toRewrite.get(key);
            toRewrite.put(key, store);
            return previous;
        }
        if (toRemove.containsKey(key)) {
            toRemove.remove(key);
            toPut.put(key, store);
            return null;
        }
        lock.readLock().tryLock();
        try {
            Storeable previous = dTable.realGet(key);
            if (previous == null) {
                toPut.put(key, store);
            } else {
                toRewrite.put(key, store);
            }
            return previous;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Storeable get(String key) {
        update();
        if (toPut.containsKey(key)) {
            return toPut.get(key);
        }
        if (toRewrite.containsKey(key)) {
            return toRewrite.get(key);
        }
        if (toRemove.containsKey(key)) {
            return null;
        }
        lock.readLock().lock();
        try {
            return dTable.realGet(key);
        } finally {
        lock.readLock().unlock();
        }
    }

    public Storeable remove(String key) {
        update();
        if (toPut.containsKey(key)) {
            Storeable previous = toPut.get(key);
            toPut.remove(key);
            return previous;
        }
        if (toRewrite.containsKey(key)) {
            Storeable previous = toRewrite.get(key);
            toRewrite.remove(key);
            return previous;
        }
        if (toRemove.containsKey(key)) {
            return null;
        }
        lock.readLock().lock();
        try {
            Storeable previous = dTable.realGet(key);
            if (previous != null) {
                toRemove.put(key, previous);
            }
            return previous;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int changes() {
        update();
        return (toPut.size() + toRewrite.size() + toRemove.size());
    }

    public void commit() {
        update();
        lock.writeLock().lock();
        try {
            localVersion++;
            dTable.version = localVersion;
            for (Map.Entry<String, Storeable> entry : toPut.entrySet()) {
                dTable.realPut(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Storeable> entry : toRewrite.entrySet()) {
                dTable.realPut(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Storeable> entry : toRemove.entrySet()) {
                dTable.realRemove(entry.getKey());
            }
            dTable.writeTable();

        } finally {
            lock.writeLock().unlock();
        }
        toPut.clear();
        toRewrite.clear();
        toRemove.clear();
    }

    public void rollback() {
        update();
        toPut.clear();
        toRewrite.clear();
        toRemove.clear();
    }

    public List<String> list() {
        update();
        List<String> listOfKeys = new LinkedList<String>();
        for (String currentKey : dTable.realDBMap.keySet()) {
            listOfKeys.add(currentKey);
        }
        for (String currentKey : toPut.keySet()) {
            listOfKeys.add(currentKey);
        }

        return listOfKeys;
    }

}
