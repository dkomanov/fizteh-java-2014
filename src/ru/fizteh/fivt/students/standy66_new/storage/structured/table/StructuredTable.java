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

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table {
    private StringTable backendTable;
    private StructuredDatabase database;
    private TableSignature tableSignature;


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

    public StringTable getBackendTable() {
        return backendTable;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        String strOldValue = backendTable.get(key);
        Storeable oldValue;
        try {
            if (strOldValue != null) {
                oldValue = database.deserialize(this, strOldValue);
            } else {
                oldValue = null;
            }
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
        String newValueString = database.serialize(this, value);
        backendTable.put(key, newValueString);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        String strValue = backendTable.get(key);
        if (strValue == null) {
            return null;
        }
        Storeable value;
        try {
            value = database.deserialize(this, strValue);
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
        backendTable.remove(key);
        return value;
    }

    @Override
    public int size() {
        return backendTable.size();
    }

    @Override
    public int commit() throws IOException {
        return backendTable.commit();
    }

    @Override
    public int rollback() {
        return backendTable.rollback();
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
    public Storeable get(String key) {
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
}
