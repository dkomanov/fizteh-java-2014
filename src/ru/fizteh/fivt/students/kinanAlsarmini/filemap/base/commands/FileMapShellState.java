package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.storage.strings.*;

import java.util.List;

public class FileMapShellState implements BaseFileMapShellState<Table, String, String> {
    public Table table = null;

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public int commit() {
        return table.commit();
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public List<String> listKeys() {
        return table.list();
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String keyToString(String key) {
        return key;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }

    @Override
    public String parseKey(String key) {
        return key;
    }

    @Override
    public String parseValue(String value) {
        return value;
    }
}
