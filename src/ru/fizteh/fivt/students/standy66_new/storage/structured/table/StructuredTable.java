package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table {
    private final StringTable backendTable;
    private final StructuredDatabase database;
    private final TableSignature tableSignature;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public StructuredTable(StringTable backendTable, StructuredDatabase database) {
        this.backendTable = backendTable;
        this.database = database;
        File signatureFile = new File(backendTable.getFile(), "signature.tsv");
        try {
            tableSignature = TableSignature.readFromFile(signatureFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("signature.tsv for table "
                    + backendTable.getName() + " doesn't exist", e);
        }
    }

    public TableSignature getTableSignature() {
        return tableSignature;
    }

    public StringTable getBackendTable() {
        return backendTable;
    }

    @Override
    public TableRow put(String key, Storeable value) throws ColumnFormatException {
        lock.writeLock().lock();
        try {
            TableRow oldValue = get(key);
            backendTable.put(key, TableRow.fromStoreable(tableSignature, value).serialize());
            return oldValue;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public TableRow remove(String key) {
        lock.writeLock().lock();
        try {
            TableRow value = get(key);
            backendTable.remove(key);
            return value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return backendTable.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        lock.writeLock().lock();
        try {
            return backendTable.commit();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        lock.writeLock().lock();
        try {
            return backendTable.rollback();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        return tableSignature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return tableSignature.getClassAt(columnIndex);
    }

    @Override
    public String getName() {
        return backendTable.getName();
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        try {
            return backendTable.list();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        lock.readLock().lock();
        try {
            return backendTable.unsavedChangesCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public TableRow get(String key) {
        lock.readLock().lock();
        try {
            String value = backendTable.get(key);
            if (value == null) {
                return null;
            }
            try {
                return database.deserialize(this, value);
            } catch (ParseException e) {
                throw new RuntimeException("ParseException occurred", e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}
