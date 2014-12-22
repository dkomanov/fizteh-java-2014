package ru.fizteh.fivt.students.ryad0m.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class TransactionTable implements Table {
    StructedTable structedTable;
    HashMap<String, Storeable> operations = new HashMap<>();
    HashSet<String> deleted = new HashSet<>();


    public TransactionTable(StructedTable structedTable) {
        this.structedTable = structedTable;
    }

    @Override
    public List<String> list() {
        Set<String> set = structedTable.getKeys();
        set.addAll(operations.keySet());
        set.removeAll(deleted);
        return new ArrayList<String>(set);
    }

    @Override
    public int size() {
        Set<String> set = structedTable.getKeys();
        set.addAll(operations.keySet());
        set.removeAll(deleted);
        return set.size();
    }

    @Override
    public int rollback() {
        int res = operations.size() + deleted.size();
        operations.clear();
        deleted.clear();
        return res;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return operations.size() + deleted.size();
    }

    @Override
    public String getName() {
        return structedTable.getName();
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        structedTable.checkIntegrity(value);
        Storeable res = get(key);
        deleted.remove(key);
        operations.put(key, ((MyStorable) value).makeCopy());
        return res;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable res = get(key);
        operations.remove(key);
        deleted.add(key);
        return res;
    }


    @Override
    public int commit() throws IOException {
        int res = operations.size() + deleted.size();
        for (Map.Entry<String, Storeable> operation : operations.entrySet()) {
            structedTable.put(operation.getKey(), operation.getValue());
        }
        deleted.forEach(structedTable::remove);
        operations.clear();
        deleted.clear();
        structedTable.save();
        return res;
    }


    @Override
    public int getColumnsCount() {
        return structedTable.getColumnTypes().size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= structedTable.getColumnTypes().size()) {
            throw new IndexOutOfBoundsException();
        }
        return structedTable.getColumnTypes().get(columnIndex);
    }

    public StructedTable getStructedTable() {
        return structedTable;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (deleted.contains(key)) {
            return null;
        } else if (operations.containsKey(key)) {
            return operations.get(key);
        } else {
            try {
                return structedTable.get(key);
            } catch (ParseException ex) {
                throw new RuntimeException();
            }
        }
    }
}
