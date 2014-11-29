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

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table, AutoCloseable {
    private StringTable backendTable;
    private StructuredDatabase database;
    private TableSignature tableSignature;
    private boolean closed = false;

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

    @Override
    public void close() throws Exception {
        if (!closed) {
            backendTable.close();
            rollback();
            closed = true;
        }
    }

    public StringTable getBackendTable() {
        assertNotClosed();
        return backendTable;
    }

    @Override
    public synchronized TableRow put(String key, Storeable value) throws ColumnFormatException {
        assertNotClosed();
        TableRow oldValue = get(key);
        backendTable.put(key, TableRow.fromStoreable(tableSignature, value).serialize());
        return oldValue;
    }

    @Override
    public synchronized TableRow remove(String key) {
        assertNotClosed();
        TableRow value = get(key);
        backendTable.remove(key);
        return value;
    }

    @Override
    public int size() {
        assertNotClosed();
        return backendTable.size();
    }

    @Override
    public int commit() throws IOException {
        assertNotClosed();
        return backendTable.commit();
    }

    @Override
    public int rollback() {
        assertNotClosed();
        return backendTable.rollback();
    }

    @Override
    public int getColumnsCount() {
        assertNotClosed();
        return tableSignature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        assertNotClosed();
        return tableSignature.getClassAt(columnIndex);
    }

    @Override
    public String getName() {
        assertNotClosed();
        return backendTable.getName();
    }

    @Override
    public List<String> list() {
        assertNotClosed();
        return backendTable.list();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        assertNotClosed();
        return backendTable.unsavedChangesCount();
    }

    @Override
    public synchronized TableRow get(String key) {
        assertNotClosed();
        String value = backendTable.get(key);
        if (value == null) {
            return null;
        }
        try {
            return database.deserialize(this, value);
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), backendTable.getFile().getAbsolutePath());
    }

    private void assertNotClosed() {
        if (closed) {
            throw new IllegalStateException("StructuredTable had been closed, but then method called");
        }
    }
}
