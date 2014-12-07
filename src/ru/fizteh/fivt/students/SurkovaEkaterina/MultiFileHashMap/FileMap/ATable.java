package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ATable implements Table {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    protected final HashMap<String, String> oldData;
    protected final HashMap<String, ValueDifference> changedData;

    private final String tableName;
    private int size;
    private int uncommittedChangesNumber;
    private String directory;

    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    public ATable(final String dir, final String tName) {
        this.directory = dir;
        this.tableName = tName;
        oldData = new HashMap<String, String>();
        changedData = new HashMap<String, ValueDifference>();
        uncommittedChangesNumber = 0;

        try {
            load();
        } catch (IOException e) {
            System.err.println("Error loading table: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading table: " + e.getMessage());
        }
    }


    public final int getUncommittedChangesNumber() {
        return uncommittedChangesNumber;
    }

    @Override
    public final String get(final String key) {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (changedData.containsKey(key)) {
            return changedData.get(key).newValue;
        }

        return oldData.get(key);
    }

    @Override
    public final String put(final String key, final String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key should not be null!");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value should not be null!");
        }
        if ((key.equals("")) || (key.trim().isEmpty())) {
            throw new IllegalArgumentException("Key should not be empty!");
        }
        if ((value.equals("") || (value.trim().isEmpty()))) {
            throw new IllegalArgumentException("Value should not be empty!");
        }

        String oldValue = getOldValue(key);
        if (oldValue == null) {
            size += 1;
        }

        addChange(key, value);
        uncommittedChangesNumber += 1;
        return oldValue;
    }

    @Override
    public final String remove(final String key) {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (get(key) == null) {
            return null;
        }
        String oldValue = getOldValue(key);
        addChange(key, null);
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChangesNumber++;
        return oldValue;
    }

    @Override
    public final List<String> list() {
        HashMap<String, String> currentData = new HashMap<String, String>(oldData);

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

    @Override
    public final int commit() {
        int recordsCommitted = 0;
        for (final String key : changedData.keySet()) {
            ValueDifference diff = changedData.get(key);
            if (diff.oldValue != diff.newValue) {
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

    @Override
    public final int rollback() {
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

    @Override
    public final String getName() {
        return tableName;
    }

    @Override
    public final int size() {
        return size;
    }

    protected final String getDirectory() {
        return directory;
    }

    private void addChange(String key, String value) {
        if (changedData.containsKey(key)) {
            changedData.get(key).newValue = value;
        } else {
            changedData.put(key, new ValueDifference(oldData.get(key), value));
        }
    }

    private String getOldValue(String key) {
        if (changedData.containsKey(key)) {
            return changedData.get(key).newValue;
        }
        return oldData.get(key);
    }

    class ValueDifference {
        public String oldValue;
        public String newValue;

        ValueDifference(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
