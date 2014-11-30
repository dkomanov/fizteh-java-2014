package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap;

import java.util.List;

public interface FileMapShellOperationsInterface<Table, Key, Value> {
    Value put(Key key, Value value);

    Value remove(Key key);

    Value get(Key key);

    List<Key> list();

    Table getTable();

    String keyToString(Key key);

    String valueToString(Value value);

    Key parseKey(String key);

    Value parseValue(String value);
}
