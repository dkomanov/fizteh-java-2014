package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TransactionModifications {
    private HashMap<String, Storeable> modifiedData;
    private ThreadSafeAbstractTable table;
    private HashSet<DatabaseFileDescriptor> modifiedFiles = new HashSet<DatabaseFileDescriptor>();

    public TransactionModifications() {
        this.modifiedData = new HashMap<String, Storeable>();
    }

    public void addModification(String key, Storeable value) {
        modifiedData.put(key, value);
    }

    public void setTable(ThreadSafeAbstractTable table) {
        this.table = table;
    }

    public int applyModifications() {
        int recordsModified = 0;
        for (final String key : modifiedData.keySet()) {
            Storeable newValue = modifiedData.get(key);
            if (!FileMapUsage.compareValues(table.oldData.get(key), newValue)) {
                if (newValue == null) {
                    table.oldData.remove(key);
                } else {
                    table.oldData.put(key, newValue);
                }
                modifiedFiles.add(table.makeDescriptor(key));
                recordsModified += 1;
            }
        }
        return recordsModified;
    }

    public int countModifications() {
        int recordsModified = 0;
        for (final String key : modifiedData.keySet()) {
            Storeable newValue = modifiedData.get(key);
            Storeable oldValue = table.oldData.get(key);
            if (!FileMapUsage.compareValues(oldValue, newValue)) {
                if (oldValue == null) {
                    ++table.size;
                } else if (newValue == null) {
                    --table.size;
                }
                recordsModified += 1;
            }
        }
        return recordsModified;
    }

    public Storeable getValue(String key) {
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key);
        }
        return table.oldData.get(key);
    }

    public int getUncommittedChanges() {
        return countModifications();
    }

    public void clear() {
        modifiedData.clear();
    }

    public Set<String> keySet() {
        return modifiedData.keySet();
    }
}
