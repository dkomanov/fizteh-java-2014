package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap;

public interface FileMapShellOperationsInterface<Table, Key, Value> {
    Value put(Key key, Value value);

    Value remove(Key key);

    Value get(Key key);

    int commit();

    int rollback();

    int size();

    Table getTable();

    String valueToString(Value value);

    Key parseKey(String key);

    Value parseValue(String value);

    int exit();
}
