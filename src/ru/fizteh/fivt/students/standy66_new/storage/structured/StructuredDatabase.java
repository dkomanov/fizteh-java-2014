package ru.fizteh.fivt.students.standy66_new.storage.structured;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabaseFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableSignature;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredDatabase implements TableProvider, AutoCloseable {
    private StringDatabase backendDatabase;
    private boolean closed = false;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public StructuredDatabase(File databaseFile) {
        StringDatabaseFactory stringDatabaseFactory = new StringDatabaseFactory();
        backendDatabase = stringDatabaseFactory.create(databaseFile);
    }

    public StringDatabase getBackendDatabase() {
        assertNotClosed();
        return backendDatabase;
    }

    @Override
    public StructuredTable getTable(String name) {
        assertNotClosed();
        lock.readLock().lock();
        try {
            return wrap(backendDatabase.getTable(name));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public StructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        assertNotClosed();
        if (columnTypes == null) {
            throw new IllegalArgumentException("columntTypes is null");
        }
        for (Class<?> cls : columnTypes) {
            if (cls == null) {
                throw new IllegalArgumentException("one of the types in columnTypes is null");
            }
        }
        if (columnTypes.size() == 0) {
            throw new IllegalArgumentException("empty type list");
        }
        lock.writeLock().lock();
        try {
            StringTable table = backendDatabase.createTable(name);
            if (table == null) {
                return null;
            }
            TableSignature tableSignature = new TableSignature(columnTypes.toArray(new Class<?>[columnTypes.size()]));
            File signatureFile = new File(table.getFile(), "signature.tsv");
            tableSignature.writeToFile(signatureFile);
            return wrap(table);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        assertNotClosed();
        lock.writeLock().lock();
        try {
            backendDatabase.removeTable(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public TableRow deserialize(Table table, String value) throws ParseException {
        assertNotClosed();
        TableSignature tableSignature = TableSignature.forTable(table);
        return TableRow.deserialize(tableSignature, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        assertNotClosed();
        TableSignature tableSignature = TableSignature.forTable(table);
        TableRow row = TableRow.fromStoreable(tableSignature, value);
        return row.serialize();
    }

    @Override
    public TableRow createFor(Table table) {
        assertNotClosed();
        TableSignature tableSignature = TableSignature.forTable(table);
        return new TableRow(tableSignature);
    }

    @Override
    public TableRow createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        assertNotClosed();
        TableSignature tableSignature = TableSignature.forTable(table);
        TableRow storeable = new TableRow(tableSignature);
        for (int i = 0; i < values.size(); i++) {
            storeable.setColumnAt(i, values.get(i));
        }
        return storeable;
    }

    @Override
    public List<String> getTableNames() {
        assertNotClosed();
        lock.readLock().lock();
        try {
            return backendDatabase.listTableNames();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), backendDatabase.getFile().getAbsolutePath());
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            backendDatabase.close();
            closed = true;
        }
    }

    private StructuredTable wrap(ru.fizteh.fivt.storage.strings.Table table) {
        if (table == null) {
            return null;
        }
        return new StructuredTable((StringTable) table, this);
    }

    private void assertNotClosed() {
        if (closed) {
            throw new IllegalStateException("StructuredDatabase had been closed, but then method called");
        }
    }
}
