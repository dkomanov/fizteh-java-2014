package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.JUnit.MyTable;

import java.util.List;

public class StoreableTable implements Table {
    public StoreableTable(MyTable givenTable, List<Class<?>> givenTypes, StoreableTableProvider givenTableProvider) {
        table = givenTable;
        types = givenTypes;
        tableProvider = givenTableProvider;
    }

    private MyTable table;
    public List<Class<?>> types;
    private StoreableTableProvider tableProvider;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Storeable get(String key) {
        return null;
    }

    @Override
    public Storeable put(String key, Storeable value) {
        return null;
    }

    @Override
    public Storeable remove(String key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<String> list() {
        return null;
    }

    @Override
    public int commit() {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }
}
