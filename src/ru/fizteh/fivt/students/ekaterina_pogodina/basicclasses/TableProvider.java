package ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses;

public interface TableProvider {

    Table getTable(String name);

    Table createTable(String name) throws Exception;

    void removeTable(String name) throws Exception;
}
