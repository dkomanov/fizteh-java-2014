package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.util.List;

/**
 * Created by kate on 08.10.14.
 */
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

    int exit();
}
