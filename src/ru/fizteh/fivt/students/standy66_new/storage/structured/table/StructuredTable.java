package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.utility.ClassUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table {
    private StringTable backendTable;
    private StructuredDatabase database;
    private TableSignature tableSignature;


    public StructuredTable(File tableFile, StringTable backendTable, StructuredDatabase database) {
        this.backendTable = backendTable;
        this.database = database;
        File signatureFile = new File(backendTable.getFile(), "signature.tsv");

        try (Scanner scanner = new Scanner(signatureFile)) {
            List<Class<?>> columnClasses = new ArrayList<>();
            while (scanner.hasNext()) {
                columnClasses.add(ClassUtility.forName(scanner.next()));
            }
            tableSignature = new TableSignature(columnClasses.toArray(new Class<?>[columnClasses.size()]));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("signature.tsv for table "
                    + backendTable.getName() + " doesn't exist", e);
        }
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
        try {
            return database.deserialize(this, backendTable.get(key));
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
    }
}
