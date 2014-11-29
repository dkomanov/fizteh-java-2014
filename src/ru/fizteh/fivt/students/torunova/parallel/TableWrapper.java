package ru.fizteh.fivt.students.torunova.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by nastya on 19.11.14.
 */
public class TableWrapper implements ru.fizteh.fivt.storage.structured.Table{
    private Table table;
    private StoreableType headOfTable;
    private TableProvider tableProvider;

    public TableWrapper(String tableName,
                        DatabaseWrapper newTableProvider, Class<?>...newTypes)
            throws IOException,
            TableNotCreatedException,
            IncorrectFileException {
        table = new Table(tableName);
        headOfTable = new StoreableType(newTypes);
        tableProvider = newTableProvider;
    }
    public TableWrapper(Table newTable, DatabaseWrapper newTableProvider, Class<?>... newTypes) {
        table = newTable;
        headOfTable = new StoreableType(newTypes);
        tableProvider = newTableProvider;
    }

    @Override
    public int hashCode() {
        if (table != null && headOfTable != null) {
            return (table.hashCode() + headOfTable.hashCode());
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableWrapper)) {
            return false;
        }
        return table.equals(((TableWrapper) obj).table) && headOfTable.equals(((TableWrapper) obj).headOfTable);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkValueFormat((StoreableType) value);
        Storeable storeableValue;
        try {
            storeableValue =  tableProvider.deserialize(this,
                    table.put(key, tableProvider.serialize(this, value)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return storeableValue;
    }

    @Override
    public Storeable remove(String key) {
        Storeable storeableValue;
        try {
            storeableValue = tableProvider.deserialize(this, table.remove(key));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return  storeableValue;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public List<String> list() {
        return table.list();
    }

    @Override
    public int commit() throws IOException {
        return table.commit();
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return table.countChangedEntries();
    }

    @Override
    public int getColumnsCount() {
        return headOfTable.getNumberOfColumns();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return headOfTable.getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Storeable get(String key) {
        Storeable storeableValue;
        try {
            storeableValue = tableProvider.deserialize(this, table.get(key));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return storeableValue;
    }

    private void checkValueFormat(StoreableType value) throws ColumnFormatException {
        if (headOfTable.getNumberOfColumns() != value.getNumberOfColumns()) {
            throw new ColumnFormatException();
        }
        int n = headOfTable.getNumberOfColumns();
        for (int i = 0; i < n; i++) {
            if (value.getColumnType(i) != headOfTable.getColumnType(i)) {
                throw new ColumnFormatException("Wrong value format");
            }
        }
    }
}
