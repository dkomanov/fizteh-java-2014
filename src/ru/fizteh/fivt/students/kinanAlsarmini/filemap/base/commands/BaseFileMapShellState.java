package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import java.util.List;

public interface BaseFileMapShellState<Table, Key, Value> {
    public Value put(Key key, Value value);

    public Value remove(Key key);

    public Value get(Key key);

    public int commit();

    public int rollback();

    public int size();

    public List<Key> listKeys();

    public Table getTable();

    public String keyToString(Key key);

    public String valueToString(Value value);

    public Key parseKey(String key);

    public Value parseValue(String value);
}
