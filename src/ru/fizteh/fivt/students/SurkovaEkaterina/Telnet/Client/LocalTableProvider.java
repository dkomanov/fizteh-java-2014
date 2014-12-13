package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class LocalTableProvider implements TableProvider {

    TableProvider provider;
    Table using;

    public LocalTableProvider(TableProvider passedProvider) {
        provider = passedProvider;
        using = null;
    }

    @Override
    public Table getTable(String name) {
        return provider.getTable(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return provider.createTable(name, columnTypes);
    }

    @Override
    public void removeTable(String name) throws IOException {
        provider.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return provider.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return provider.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return provider.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return provider.createFor(table);
    }

    @Override
    public List<String> getTableNames() {
        return provider.getTableNames();
    }

    public Table getUsing() {
        return using;
    }

    public Table use(String name) {
        using = provider.getTable(name);
        return using;
    }

}
