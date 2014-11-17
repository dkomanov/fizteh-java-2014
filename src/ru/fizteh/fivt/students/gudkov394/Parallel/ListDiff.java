package ru.fizteh.fivt.students.gudkov394.Parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.gudkov394.Storable.src.CurrentTable;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kagudkov on 17.11.14.
 */
public class ListDiff {
    private Map<String, String> newKey = null;
    private Map<String, String> removedKey = null;
    private Map<String, String> changedKey = null;
    ReentrantReadWriteLock readWriteLock = null;
    Lock readLock = null;
    Lock writeLock = null;
    CurrentTable table = null;

    public ListDiff(CurrentTable tableTmp, ReentrantReadWriteLock lockTmp) {
        readWriteLock = lockTmp;
        readLock = lockTmp.readLock();
        writeLock = lockTmp.writeLock();
        table = tableTmp;
        newKey = new HashMap<String, String>();
        removedKey = new HashMap<String, String>();
        changedKey = new HashMap<String, String>();
    }

    public String get(String key) {
        if (newKey.containsKey(key)) {
            return newKey.get(key);
        } else if (changedKey.containsKey(key)) {
            return changedKey.get(key);
        } else if (removedKey.containsKey(key)) {
            return null;
        } else {
            readLock.lock();
            try {
                if (table.get(key) == null) {
                    return null;
                }
                return table.getTableProviderClass().serialize(table, table.get(key));
            } finally {
                readLock.unlock();
            }
        }
    }

    public String put(String key, String value) {
        if (newKey.containsKey(key)) {
            return newKey.put(key, value);
        } else if (removedKey.containsKey(key)) {
            return removedKey.put(key, value);
        } else if (changedKey.containsKey(key)) {
            return changedKey.put(key, value);
        } else {
            readLock.lock();
            try {
                Storeable valueFromTable = table.get(key);
                if (valueFromTable == null) {
                    newKey.put(key, value);
                    return null;
                } else {
                    changedKey.put(key, value);
                    return table.getTableProviderClass().serialize(table, valueFromTable);
                }
            } finally {
                readLock.unlock();
            }
        }
    }

    public String remove(String key) {
        if (newKey.containsKey(key)) {
            return newKey.remove(key);
        } else if (removedKey.containsKey(key)) {
            return null;
        } else if (changedKey.containsKey(key)) {
            return changedKey.remove(key);
        } else {
            readLock.lock();
            try {
                Storeable valueFromTable = table.get(key);
                if (valueFromTable == null) {
                    return null;
                } else {
                    removedKey.put(key, null);
                    return table.getTableProviderClass().serialize(table, valueFromTable);
                }
            } finally {
                readLock.unlock();
            }
        }
    }

    public int commit() throws ParseException {
        writeLock.lock();
        try {
            for (String s : newKey.keySet()) {
                table.put(s, table.getTableProviderClass().deserialize(table, newKey.get(s)));
            }
            for (String s : changedKey.keySet()) {
                table.put(s, table.getTableProviderClass().deserialize(table, changedKey.get(s)));
            }
            for (String s : removedKey.keySet()) {
                table.remove(s);
            }
            table.commit();
            return changedKey.size();
        } finally {
            clear();
            writeLock.unlock();
        }
    }

    public int rollback() {
        try {
            return newKey.size() + changedKey.size() + removedKey.size();
        } finally {
            clear();
        }
    }

    private void clear() {
        removedKey.clear();
        changedKey.clear();
        newKey.clear();
    }

    public int changedSize() {
        return newKey.size() - removedKey.size();
    }
}
