package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;

import java.util.List;

public class StoreableTableProvider implements TableProvider {
    public Table getTable(String name) {
        return null;
    }

    public Table createTable(String name, List<Class<?>> columnTypes) {
        return null;
    }

    public void removeTable(String name) {

    }

    public Storeable deserialize(Table table, String value) {
        return null;
    }

    public String serialize(Table table, Storeable value) {
        return null;
    }

    public Storeable createFor(Table table) {
        return null;
    }

    public Storeable createFor(Table table, List<?> values) {
        return null;
    }

    public List<String> getTableNames() {
        return null;
    }
}
