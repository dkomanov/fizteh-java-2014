package ru.fizteh.fivt.students.gudkov394.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class RemoteTableProviderClass implements RemoteTableProvider {

    @Override
    public void close() throws IOException {

    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return null;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }
}
