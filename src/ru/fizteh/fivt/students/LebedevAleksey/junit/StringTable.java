package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;

import java.util.List;

public class StringTable implements ru.fizteh.fivt.storage.strings.Table {
    private Table table;

    public StringTable(Table table) {
        this.table = table;
    }

    private void checkKeyNotNull(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument \"key\" is null");
        }
    }

    private void checkKeyValueNotNull(String key, String value) {
        checkKeyNotNull(key);
        if (value == null) {
            throw new IllegalArgumentException("Argument \"value\" is null");
        }
    }

    @Override
    public String getName() {
        return table.getTableName();
    }

    @Override
    public String get(String key) {
        checkKeyNotNull(key);
        try {
            return table.get(key);
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public String put(String key, String value) {
        checkKeyValueNotNull(key, value);
        try {
            return table.put(key, value);
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public String remove(String key) {
        checkKeyNotNull(key);
        try {
            return table.getAndRemove(key);
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int size() {
        try {
            return table.count();
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int commit() {
        try {
            return table.commit();
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public List<String> list() {
        try {
            return table.list();
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }

    }
}
