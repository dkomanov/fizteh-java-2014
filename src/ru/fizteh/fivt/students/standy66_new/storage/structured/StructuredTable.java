package ru.fizteh.fivt.students.standy66_new.storage.structured;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table {
    private StringTable backendTable;
    private File tableFile;
    private StructuredDatabase database;


    public StructuredTable(File tableFile, StringTable backendTable, StructuredDatabase database) {
        this.backendTable = backendTable;
        this.tableFile = tableFile;
        this.database = database;
        //TODO: read signature.tsv
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        Storeable oldValue;
        try {
            oldValue = database.deserialize(this, backendTable.get(key));
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
        String newValueString = database.serialize(this, value);
        backendTable.put(key, newValueString);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        Storeable value;
        try {
            value = database.deserialize(this, backendTable.get(key));
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
        //TODO: not implemented
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        //TODO: not implemented
        return null;
    }

    @Override
    public String getName() {
        //TODO: not implemented
        return backendTable.getName();
    }

    @Override
    public Storeable get(String key) {
        try {
            return database.deserialize(this, backendTable.get(key));
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
    }
}
