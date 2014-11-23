package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ATable implements Table {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    protected final HashMap<String, Storeable> oldData;
    protected final HashMap<String, ValueDifference> changedData;

    private final String tableName;
    private int size;
    private int uncommittedChangesNumber;
    protected final String directory;

    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    public ATable(final String dir, final String tName) {
        this.directory = dir;
        this.tableName = tName;
        oldData = new HashMap<String, Storeable>();
        changedData = new HashMap<String, ValueDifference>();
        uncommittedChangesNumber = 0;

        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid file format!");
        }
    }

    public Storeable get(String key) {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if (changedData.containsKey(key)) {
            return changedData.get(key).newValue;
        }

        return oldData.get(key);
    }

    public Storeable put(String key, Storeable value) {
        Storeable oldValue = getOldValue(key);
        if (oldValue == null) {
            size += 1;
        }
        addChange(key, value);
        uncommittedChangesNumber += 1;
        return oldValue;
    }

    public Storeable remove(String key) {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if (get(key) == null) {
            return null;
        }
        Storeable oldValue = getOldValue(key);
        addChange(key, null);
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChangesNumber++;
        return oldValue;
    }

    public List<String> list() {
        HashMap<String, Storeable> currentData = new HashMap<String, Storeable>(oldData);

        for (final String key : changedData.keySet()) {
            ValueDifference diff = changedData.get(key);
            if (diff.oldValue != diff.newValue) {
                if (diff.newValue == null) {
                    currentData.remove(key);
                } else {
                    currentData.put(key, diff.newValue);
                }
            }
        }

        List<String> list = new ArrayList<String>(currentData.keySet());

        return list;
    }

    public String getDirectory() {
        return directory;
    }

    public int commit() {
        int recordsCommitted = 0;
        for (final String key : changedData.keySet()) {
            ValueDifference diff = changedData.get(key);
            if (!equal(diff.oldValue, diff.newValue)) {
                if (diff.newValue == null) {
                    oldData.remove(key);
                } else {
                    oldData.put(key, diff.newValue);
                }
                recordsCommitted++;
            }
        }
        changedData.clear();
        size = oldData.size();
        try {
            save();
        } catch (IOException e) {
            System.err.println("commit: " + e.getMessage());
            return 0;
        }
        uncommittedChangesNumber = 0;

        return recordsCommitted;
    }

    public int rollback() {
        int recordsDeleted = 0;
        for (final String key : changedData.keySet()) {
            ValueDifference diff = changedData.get(key);
            if (diff.oldValue != diff.newValue) {
                recordsDeleted += 1;
            }
        }
        changedData.clear();
        size = oldData.size();

        uncommittedChangesNumber = 0;

        return recordsDeleted;
    }

    public String getName() {
        return tableName;
    }

    public int size() {
        return size;
    }

    private void addChange(String key, Storeable value) {
        if (changedData.containsKey(key)) {
            changedData.get(key).newValue = value;
        } else {
            changedData.put(key, new ValueDifference(oldData.get(key), value));
        }
    }

    private Storeable getOldValue(String key) {
        if (changedData.containsKey(key)) {
            return changedData.get(key).newValue;
        }
        return oldData.get(key);
    }

    private boolean equal(Object key1, Object key2) {
        if (key1 == null && key2 == null) {
            return true;
        }
        if (key1 == null || key2 == null) {
            return false;
        }
        return key1.equals(key2);
    }

    class ValueDifference {
        public Storeable oldValue;
        public Storeable newValue;

        ValueDifference(Storeable oldValue, Storeable newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
