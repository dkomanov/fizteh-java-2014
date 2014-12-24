package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import java.util.List;

public interface BaseFileMapShellState<Table, Key, Value> {
    Value put(Key key, Value value);

    Value remove(Key key);

    Value get(Key key);

    int commit();

    int rollback();

    int size();

    List<Key> listKeys();

    Table getTable();

    String keyToString(Key key);

    String valueToString(Value value);

    Key parseKey(String key);

    Value parseValue(String value);
}
