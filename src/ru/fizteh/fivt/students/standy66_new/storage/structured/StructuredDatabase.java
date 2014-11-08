package ru.fizteh.fivt.students.standy66_new.storage.structured;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableSignature;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredDatabase implements TableProvider {
    private StringDatabase backendDatabase;

    public StructuredDatabase(File databaseFile) {
        backendDatabase = new StringDatabase(databaseFile);
    }

    @Override
    public Table getTable(String name) {
        return wrap(backendDatabase.getTable(name));
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        backendDatabase.createTable(name);
        TableSignature tableSignature = new TableSignature(columnTypes.toArray(new Class<?>[columnTypes.size()]));

        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {
        backendDatabase.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        //TODO: not implemented
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        //TODO: not implemented
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        TableSignature tableSignature = TableSignature.forTable(table);
        return new TableRow(tableSignature);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        TableSignature tableSignature = TableSignature.forTable(table);
        Storeable storeable = new TableRow(tableSignature);
        for (int i = 0; i < values.size(); i++) {
            storeable.setColumnAt(i, values.get(i));
        }
        return storeable;
    }

    private StructuredTable wrap(ru.fizteh.fivt.storage.strings.Table table) {
        return new StructuredTable((StringTable) table, this);
    }
}
