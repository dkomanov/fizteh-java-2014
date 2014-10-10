/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import ru.fizteh.fivt.storage.strings.Table;

/**
 *
 * @author shakarim
 */
public abstract class AbstractTable implements Table {

    protected HashMap<String, String> table = new HashMap<>();
    protected HashMap<String, String> old = new HashMap<>();
    private String tableName = null;

    public AbstractTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null");
        }
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null");
        }
        String oldValue = table.put(key, value);
        if (!old.containsKey(key)) {
            old.put(key, oldValue);
        } else if (value.equals(old.get(key))) {
            old.remove(key);
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null");
        }
        if (!table.containsKey(key)) {
            return null;
        }

        String oldValue = table.remove(key);

        if (old.containsKey(key) && old.get(key) == null) {
            old.remove(key);
        } else if (!old.containsKey(key)) {
            old.put(key, oldValue);
        }
        return oldValue;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public int commit() {
        int uncommited = old.size();
        if (uncommited == 0) {
            return 0;
        }

        try {
            save();
            old.clear();
        } catch (IOException ex) {
            //
        }
        return uncommited;
    }

    @Override
    public int rollback() {
        for (Entry<String, String> entry : old.entrySet()) {
            table.put(entry.getKey(), entry.getValue());
        }
        int uncommited = old.size();
        old.clear();
        return uncommited;
    }

    @Override
    public List<String> list() {
        List<String> retVal = new ArrayList<>();
        for (Entry<String, String> entry : table.entrySet()) {
            retVal.add(entry.getKey());
        }
        return retVal;
    }

    public int changes() {
        return old.size();
    }

    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;
}
