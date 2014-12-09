package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap;

import java.util.List;

public class FileMapShellOperations implements FileMapShellOperationsInterface<ATable, String, String>  {
    public ATable table = null;

    public final String put(final String key, final String value) {
        return table.put(key, value);
    }

    public final String remove(final String key) {
        return table.remove(key);
    }

    public final String get(final String key) {
        return table.get(key);
    }

    public final List<String> list() {
        return table.list();
    }

    public final ATable getTable() {
        return table;
    }

    public final String keyToString(final String key) {
        return key;
    }

    public final String valueToString(final String value) {
        return value;
    }

    public final String parseKey(final String key) {
        return key;
    }

    public final String parseValue(final String value) {
        return value;
    }
}
