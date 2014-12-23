package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.util.List;

/**
 * Created by luba_yaronskaya on 27.10.14.
 */
public class CheckParameters {
    public static void checkKey(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
    }

    public static void checkValue(String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
    }

    public static void checkValue(Storeable value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
    }

    public static void checkNumberOfArguments(String[] args, int legalNumber) {
        if (args.length != legalNumber) {
            throw new IllegalArgumentException("illegal number of arguments");
        }
    }

    public static void checkTableName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (name.contains(File.separator)) {
            throw new IllegalArgumentException("table name contains separator");
        }
        if (name.contains(".") || name.contains(";") || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("illegal table name");
        }
    }

    public static void checkColumnTypesList(List<Class<?>> columnTypes) {
        if (columnTypes == null) {
            throw new IllegalArgumentException("column types list is null");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types list is empty");
        }
    }

    public static void checkMatchItemToTable(Table table, Storeable value) {
        try {
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                    throw new ColumnFormatException("wrong type (Expected: "
                            + table.getColumnType(i).toString() + " Actual: "
                            + value.getColumnAt(i).getClass().toString());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal column count");
        }
    }
}
