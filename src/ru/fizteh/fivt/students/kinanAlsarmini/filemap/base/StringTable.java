package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base;

import java.util.List;
import java.util.ArrayList;

import ru.fizteh.fivt.storage.strings.Table;

public abstract class StringTable extends AbstractStorage<String, String> implements Table {
    protected StringTable(String directory, String tableName) {
        super(directory, tableName);
    }

    @Override
    public String get(String key) {
        return storageGet(key);
    }

    @Override
    public String put(String key, String value) {
        return storagePut(key, value);
    }

    @Override
    public String remove(String key) {
        return storageRemove(key);
    }

    @Override
    public List<String> list() {
        return storageListKeys();
    }

    @Override
    public int size() {
        return storageSize();
    }

    @Override
    public int commit() {
        return storageCommit();
    }

    @Override
    public int rollback() {
        return storageRollback();
    }
}
