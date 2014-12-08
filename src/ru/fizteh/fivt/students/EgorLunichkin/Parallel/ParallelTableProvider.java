package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.*;

import java.util.List;

public class ParallelTableProvider implements TableProvider {
    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        return null;
    }

    @Override
    public void removeTable(String name) {

    }

    @Override
    public Storeable deserialize(Table table, String value) {
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) {
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return null;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        return null;
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }
}
