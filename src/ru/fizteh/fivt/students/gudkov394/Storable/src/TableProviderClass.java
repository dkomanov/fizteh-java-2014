package ru.fizteh.fivt.students.gudkov394.Storable.src;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kagudkov on 25.10.14.
 */
public class TableProviderClass implements TableProvider {
    public Map<String, CurrentTable> tables = new HashMap<String, CurrentTable>();
    MySerialize serialize = new MySerialize();

    public TableProviderClass() {
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            return null;
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            CurrentTable newTable = new CurrentTable(name, (ArrayList<Class<?>>) columnTypes);
            tables.put(name, newTable);
            newTable.tableProviderClass = this;
            return newTable;
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            tables.get(name).delete();
            tables.remove(name);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return serialize.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return serialize.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new TableContents(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Object> valuesTmp = new ArrayList<>(values);
        for (int i = 0; i < valuesTmp.size(); ++i) {
            if (valuesTmp.get(i).getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("wrong column type");
            }
        }
        return new TableContents(valuesTmp);
    }

    @Override
    public List<String> getTableNames() {
        return (List<String>) tables.keySet();
    }

    public void put(String tmp, CurrentTable ct) {
        tables.put(tmp, ct);
    }
}
