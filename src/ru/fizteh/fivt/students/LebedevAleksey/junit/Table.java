package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;

import java.util.*;
import java.util.function.Consumer;

public class Table extends ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.Table {
    private Map<String, String> changedKeys = new TreeMap<>();

    public Table(String name, ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.Database databaseParent) {
        super(name, databaseParent);
    }

    @Override
    public boolean remove(String key) throws LoadOrSaveException, DatabaseFileStructureException {
        String value = super.get(key);
        String oldValue = get(key);
        if (value != null) {
            if (oldValue != null) {
                changedKeys.put(key, null);
            }
        } else {
            changedKeys.remove(key);
        }
        return (oldValue != null);
    }

    public String getAndRemove(String key) throws DatabaseFileStructureException, LoadOrSaveException {
        String oldValue = get(key);
        remove(key);
        return oldValue;
    }

    @Override
    public String get(String key) throws LoadOrSaveException, DatabaseFileStructureException {
        if (changedKeys.containsKey(key)) {
            return changedKeys.get(key);
        } else {
            return super.get(key);
        }
    }

    @Override
    public String put(String key, String value) throws LoadOrSaveException, DatabaseFileStructureException {
        String oldValue = get(key);
        if (value.equals(super.get(key))) {
            changedKeys.remove(key);
        } else {
            changedKeys.put(key, value);
        }
        return oldValue;
    }

    @Override
    public void save() throws LoadOrSaveException, DatabaseFileStructureException {
        for (String key : changedKeys.keySet()) {
            String value = changedKeys.get(key);
            if (value == null) {
                super.remove(key);
            } else {
                super.put(key, value);
            }
        }
        changedKeys.clear();
        super.save();
    }

    @Override
    public int count() throws LoadOrSaveException, DatabaseFileStructureException {
        int deletedCount = 0;
        int addedCount = 0;
        for (String key : changedKeys.keySet()) {
            String value = changedKeys.get(key);
            if (value == null) {
                ++deletedCount;
            } else {
                if (super.get(key) == null) {
                    addedCount++;
                }
            }
        }
        return super.count() + addedCount - deletedCount;
    }

    @Override
    public ArrayList<String> list() throws LoadOrSaveException, DatabaseFileStructureException {
        Set<String> items = new TreeSet<>(super.list());
        for (String key : changedKeys.keySet()) {
            String value = changedKeys.get(key);
            if (value == null) {
                items.remove(key);
            } else {
                items.add(key);
            }
        }
        ArrayList<String> result = new ArrayList<>(items.size());
        items.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                result.add(s);
            }
        });
        return result;
    }

    public int changesCount() {
        return changedKeys.size();
    }

    public int commit() throws DatabaseFileStructureException, LoadOrSaveException {
        int changes = changesCount();
        save();
        return changes;
    }

    public int rollback() {
        int changes = changesCount();
        changedKeys.clear();
        initParts();
        return changes;
    }
}
