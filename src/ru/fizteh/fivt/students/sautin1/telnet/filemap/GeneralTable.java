package ru.fizteh.fivt.students.sautin1.telnet.filemap;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A typical database table class.
 * Created by sautin1 on 10/10/14.
 */
public class GeneralTable<MappedValue> implements Iterable<Map.Entry<String, MappedValue>> {
    private Map<String, MappedValue> committedEntries;
    private final String name;
    protected final boolean autoCommit;
    private final TableProvider provider;
    private ThreadLocal<DiffAccounter> localDiffAccounter;
    private Lock lockCommit;

    private class DiffAccounter {
        private Map<String, MappedValue> addedEntries;
        private Map<String, MappedValue> overwrittenEntries;
        private Set<String> deletedEntries;

        DiffAccounter() {
            addedEntries = new HashMap<>();
            overwrittenEntries = new HashMap<>();
            deletedEntries = new HashSet<>();
        }
    }

    public GeneralTable(String name, boolean autoCommit, TableProvider provider) {
        if (name == null) {
            throw new IllegalArgumentException("No table name provided");
        }
        this.name = name;
        this.autoCommit = autoCommit;
        this.provider = provider;
        committedEntries = new HashMap<>();
        lockCommit = new ReentrantLock();
        localDiffAccounter = ThreadLocal.withInitial(DiffAccounter::new);
    }

    public GeneralTable(String name, TableProvider provider) {
        this(name, true, provider);
    }

    /**
     * Get size of the current table.
     * @return size of the current table.
     */
    public int size() {
        DiffAccounter diffAccounter = localDiffAccounter.get();
        return committedEntries.size() + diffAccounter.addedEntries.size() - diffAccounter.deletedEntries.size();
    }

    /**
     * Get the number of differences between the current table now and the last commit.
     * @return the number of differences between the current table now and the last commit.
     */
    public int getNumberOfUncommittedChanges() {
        DiffAccounter diffAccounter = localDiffAccounter.get();
        return diffAccounter.addedEntries.size() + diffAccounter.overwrittenEntries.size()
                + diffAccounter.deletedEntries.size();
    }

    /**
     * Get the name of the table.
     * @return name of the table.
     */
    public String getName() {
        return name;
    }

    /**
     * Add a new entry to the table.
     * @param key - key of the entry.
     * @param value - value of the entry.
     * @return overwritten value, if the key existed in the table. null, otherwise.
     */
    public MappedValue put(String key, MappedValue value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null key is not allowed");
        }
        DiffAccounter diffAccounter = localDiffAccounter.get();
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = diffAccounter.addedEntries.get(key);
        MappedValue overwrittenValue = diffAccounter.overwrittenEntries.get(key);
        boolean isDeleted = diffAccounter.deletedEntries.contains(key);
        boolean isCommitted = (committedValue != null);
        boolean isAdded = (addedValue != null);
        boolean isOverwritten = (overwrittenValue != null);

        MappedValue returnValue;
        if (isCommitted && !isDeleted && !isOverwritten) {
            // overwrite existing old entry (key-value pair)
            if (!committedValue.equals(value)) {
                diffAccounter.overwrittenEntries.put(key, value);
            }
            returnValue = committedValue;
        } else if (!isCommitted && !isAdded) {
            // add completely new entry
            returnValue = diffAccounter.addedEntries.put(key, value);
        } else if (isDeleted) {
            // add recently deleted entry
            diffAccounter.deletedEntries.remove(key);
            if (isCommitted && !committedValue.equals(value)) {
                diffAccounter.overwrittenEntries.put(key, value);
            }
            returnValue = null;
        } else if (isAdded) {
            // overwrite recently added entries
            if (!addedValue.equals(value)) {
                diffAccounter.addedEntries.put(key, value);
            }
            returnValue = addedValue;
        } else {
            // overwrite already overwritten entry
            if (!overwrittenValue.equals(value)) {
                diffAccounter.overwrittenEntries.put(key, value);
            }
            returnValue = overwrittenValue;
        }

        if (autoCommit) {
            commit();
        }

        return returnValue;
    }

    /**
     * Get an existing entry from the table.
     * @param key - key of the entry.
     * @return value, corresponding to the key.
     */
    public MappedValue get(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null key is not allowed");
        }
        DiffAccounter diffAccounter = localDiffAccounter.get();
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = diffAccounter.addedEntries.get(key);
        MappedValue overwrittenValue = diffAccounter.overwrittenEntries.get(key);
        boolean isDeleted = diffAccounter.deletedEntries.contains(key);

        MappedValue returnValue = null;
        if (addedValue != null) {
            returnValue = addedValue;
        } else if (overwrittenValue != null) {
            returnValue = overwrittenValue;
        } else if (!isDeleted && committedValue != null) {
            returnValue = committedValue;
        }
        return returnValue;
    }

    /**
     * Remove an entry from the table.
     * @param key - key of the entry.
     * @return value, corresponding to the key.
     */
    public MappedValue remove(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null key is not allowed");
        }
        DiffAccounter diffAccounter = localDiffAccounter.get();
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = diffAccounter.addedEntries.get(key);
        MappedValue overwrittenValue = diffAccounter.overwrittenEntries.get(key);
        boolean isDeleted = diffAccounter.deletedEntries.contains(key);
        boolean isCommitted = (committedValue != null);
        boolean isAdded = (addedValue != null);
        boolean isOverwritten = (overwrittenValue != null);

        MappedValue returnValue = null;

        if (isCommitted && !isDeleted) {
            diffAccounter.deletedEntries.add(key);
            if (isOverwritten) {
                returnValue = overwrittenValue;
            } else {
                returnValue = committedValue;
            }
        } else if (isAdded) {
            diffAccounter.addedEntries.remove(key);
            returnValue = addedValue;
        }

        if (autoCommit) {
            commit();
        }

        return returnValue;
    }

    /**
     * Commit changes made to the table.
     * @return the number of changes made to the table since the last commit.
     */
    public int commit() {
        try {
            lockCommit.lock();
            DiffAccounter diffAccounter = localDiffAccounter.get();
            for (Map.Entry<String, MappedValue> addedEntry : diffAccounter.addedEntries.entrySet()) {
                committedEntries.put(addedEntry.getKey(), addedEntry.getValue());
            }
            for (Map.Entry<String, MappedValue> overwrittenEntry : diffAccounter.overwrittenEntries.entrySet()) {
                committedEntries.put(overwrittenEntry.getKey(), overwrittenEntry.getValue());
            }
            for (String deletedKey : diffAccounter.deletedEntries) {
                committedEntries.remove(deletedKey);
            }
            return getNumberOfUncommittedChanges();
        } finally {
            DiffAccounter diffAccounter = localDiffAccounter.get();
            diffAccounter.addedEntries.clear();
            diffAccounter.overwrittenEntries.clear();
            diffAccounter.deletedEntries.clear();
            lockCommit.unlock();
        }
    }

    /**
     * Reject all the changes made to the table after the last commit.
     * @return the number of changes rejected.
     */
    public int rollback() {
        int diffCounter = getNumberOfUncommittedChanges();
        DiffAccounter diffAccounter = localDiffAccounter.get();
        diffAccounter.addedEntries.clear();
        diffAccounter.overwrittenEntries.clear();
        diffAccounter.deletedEntries.clear();
        return diffCounter;
    }

    /**
     * List all the keys stored in the table.
     * @return List of keys.
     */
    public List<String> list() {
        List<String> keyList = new ArrayList<>();
        DiffAccounter diffAccounter = localDiffAccounter.get();
        keyList.addAll(diffAccounter.addedEntries.keySet());
        keyList.addAll(diffAccounter.overwrittenEntries.keySet());
        keyList.addAll(committedEntries.keySet());
        keyList.removeAll(diffAccounter.deletedEntries);
        return keyList;
    }

    /**
     * Iterator to a table entry.
     * @return Iterator to a table entry.
     */
    @Override
    public Iterator<Map.Entry<String, MappedValue>> iterator() {
        return committedEntries.entrySet().iterator();
    }
}
