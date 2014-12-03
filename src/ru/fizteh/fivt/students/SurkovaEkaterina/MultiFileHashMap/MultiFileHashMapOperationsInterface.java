package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

public interface MultiFileHashMapOperationsInterface<Table> {
    Table useTable(String name);

    void dropTable(String name);

    Table createTable(String parameters);

    String getTableName();

    void showTables();

    int exit();
}
