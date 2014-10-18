package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.util.List;

public class FileMapShellOperations implements
        FileMapShellOperationsInterface<ATable, String, String> {
    public ATable table = null;

    @Override
    public final String put(final String key, final String value) {
        return table.put(key, value);
    }

    @Override
    public final String remove(final String key) {
        return table.remove(key);
    }

    @Override
    public final String get(final String key) {
        return table.get(key);
    }

    @Override
    public final List<String> list() {
        return table.list();
    }

    @Override
    public final ATable getTable() {
        return table;
    }

    @Override
    public final String keyToString(final String key) {
        return key;
    }

    @Override
    public final String valueToString(final String value) {
        return value;
    }

    @Override
    public final String parseKey(final String key) {
        return key;
    }

    @Override
    public final String parseValue(final String value) {
        return value;
    }

    @Override
    public final int exit() {
        return table.exit();
    }
}
