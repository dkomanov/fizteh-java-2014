package ru.fizteh.fivt.students.sautin1.filemap;

import java.util.*;

/**
 * A typical database table class.
 * Created by sautin1 on 10/10/14.
 */
public class GeneralTable<MappedValue> implements Iterable<Map.Entry<String, MappedValue>> {
    private Map<String, MappedValue> committedEntries;
    private Map<String, MappedValue> addedEntries;
    private Map<String, MappedValue> overwrittenEntries;
    private Set<String> deletedEntries;
    private final String name;
    protected final boolean autoCommit;

    GeneralTable(String name, boolean autoCommit) {
        if (name == null) {
            throw new IllegalArgumentException("No table name provided");
        }
        this.name = name;
        this.autoCommit = autoCommit;
        committedEntries = new HashMap<>();
        addedEntries = new HashMap<>();
        overwrittenEntries = new HashMap<>();
        deletedEntries = new HashSet<>();
    }

    GeneralTable(String name) {
        this(name, true);
    }

    /**
     * Get size of the current table.
     * @return size of the current table.
     */
    public int size() {
        return committedEntries.size() + addedEntries.size() - deletedEntries.size();
    }

    /**
     * Get the number of differences between the current table now and the last commit.
     * @return the number of differences between the current table now and the last commit.
     */
    public int diffCount() {
        return addedEntries.size() + overwrittenEntries.size() + deletedEntries.size();
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
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = addedEntries.get(key);
        MappedValue overwrittenValue = overwrittenEntries.get(key);
        boolean isDeleted = deletedEntries.contains(key);
        boolean isCommitted = (committedValue != null);
        boolean isAdded = (addedValue != null);
        boolean isOverwritten = (overwrittenValue != null);

        MappedValue returnValue;
        if (isCommitted && !isDeleted && !isOverwritten) {
            // overwrite existing old entry (key-value pair)
            if (!committedValue.equals(value)) {
                overwrittenEntries.put(key, value);
            }
            returnValue = committedValue;
        } else if (!isCommitted && !isAdded) {
            // add completely new entry
            returnValue = addedEntries.put(key, value);
        } else if (isDeleted) {
            // add recently deleted entry
            deletedEntries.remove(key);
            if (isCommitted && !committedValue.equals(value)) {
                overwrittenEntries.put(key, value);
            }
            returnValue = null;
        } else if (isAdded) {
            // overwrite recently added entries
            if (!addedValue.equals(value)) {
                addedEntries.put(key, value);
            }
            returnValue = addedValue;
        } else {
            // isOverwritten overwrite already overwritten entry
            if (!overwrittenValue.equals(value)) {
                overwrittenEntries.put(key, value);
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
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = addedEntries.get(key);
        MappedValue overwrittenValue = overwrittenEntries.get(key);
        boolean isDeleted = deletedEntries.contains(key);

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
        MappedValue committedValue = committedEntries.get(key);
        MappedValue addedValue = addedEntries.get(key);
        MappedValue overwrittenValue = overwrittenEntries.get(key);
        boolean isDeleted = deletedEntries.contains(key);
        boolean isCommitted = (committedValue != null);
        boolean isAdded = (addedValue != null);
        boolean isOverwritten = (overwrittenValue != null);

        MappedValue returnValue = null;

        if (isCommitted && !isDeleted) {
            deletedEntries.add(key);
            if (isOverwritten) {
                returnValue = overwrittenValue;
            } else {
                returnValue = committedValue;
            }
        } else if (isAdded) {
            addedEntries.remove(key);
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
        for (Map.Entry<String, MappedValue> addedEntry : addedEntries.entrySet()) {
            committedEntries.put(addedEntry.getKey(), addedEntry.getValue());
        }
        for (Map.Entry<String, MappedValue> overwrittenEntry : overwrittenEntries.entrySet()) {
            committedEntries.put(overwrittenEntry.getKey(), overwrittenEntry.getValue());
        }
        for (String deletedKey : deletedEntries) {
            committedEntries.remove(deletedKey);
        }
        int diffCounter = diffCount();
        addedEntries.clear();
        overwrittenEntries.clear();
        deletedEntries.clear();
        return diffCounter;
    }

    /**
     * Reject all the changes made to the table after the last commit.
     * @return the number of changes rejected.
     */
    public int rollback() {
        int diffCounter = diffCount();
        addedEntries.clear();
        overwrittenEntries.clear();
        deletedEntries.clear();
        return diffCounter;
    }

    /**
     * List all the keys stored in the table.
     * @return List of keys.
     */
    public List<String> list() {
        List<String> keyList = new ArrayList<>();
        keyList.addAll(addedEntries.keySet());
        keyList.addAll(overwrittenEntries.keySet());
        keyList.addAll(committedEntries.keySet());
        keyList.removeAll(deletedEntries);
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
