package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class DataBase implements TableProvider, AutoCloseable {
    private TableHolder tableHolder;
    private Table activeTable;

    public DataBase(TableHolder tableHolder) {
        this.tableHolder = tableHolder;
    }
    public Table getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(Table activeTable) {
        this.activeTable = activeTable;
    }

    public final void checkActiveTable() {
        if (activeTable == null) {
            throw new NoActiveTableException();
        }
    }

    @Override
    public Table getTable(String name) {
        return tableHolder.getTable(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return tableHolder.createTable(name, columnTypes);
    }

    @Override
    public void removeTable(String name) throws IOException {
        tableHolder.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return tableHolder.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return tableHolder.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return tableHolder.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return tableHolder.createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return tableHolder.getTableNames();
    }

    @Override
    public void close() {
        tableHolder.close();
    }
}
