package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ATable {

    protected static final Charset CHARSET = StandardCharsets.UTF_8;

    protected final HashMap<String, String> data;

    private final String tableName;
    private int size;
    private String directory;

    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    public ATable(final String dir, final String tName) {
        this.directory = dir;
        this.tableName = tName;
        data = new HashMap<String, String>();
        try {
            load();
        } catch (IOException e) {
            System.err.println("Error loading table: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading table: " + e.getMessage());
        }
    }

    public final String get(final String key) {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("Key cannot be empty!");
        }
        return data.get(key);
    }

    public final String put(final String key, final String value) {
        if ((key == null) || (value == null)) {
            String message = (key == null) ? "Key " : "Value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }
        if ((key.equals("")) || (value.equals(""))
                || (key.trim().isEmpty()) || (value.trim().isEmpty())) {
            String message = (key.equals("")) ? "Key " : "Value ";
            throw new IllegalArgumentException(message + "cannot be empty");
        }
        String oldValue = data.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        return oldValue;
    }

    public final String remove(final String key) {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (get(key) == null) {
            return null;
        }
        String oldValue = data.remove(key);
        if (oldValue != null) {
            size -= 1;
        }

        return oldValue;
    }

    public final List<String> list() {
        List<String> list = new ArrayList<String>(data.keySet());
        return list;
    }

    public final int exit() {
        try {
            save();
        } catch (IOException e) {
            System.err.println("exit: " + e.getMessage());
            return -1;
        }
        return 0;
    }

    protected final String getDirectory() {
        return directory;
    }
}
