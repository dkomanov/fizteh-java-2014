package ru.fizteh.fivt.students.lukina.DataBase;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;

public class DBaseRow implements Storeable {
    private ArrayList<Object> list;
    private Table table;

    public DBaseRow(Table t) {
        list = new ArrayList<>();
        table = t;
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            list.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (value != null && !(value.getClass().equals(table.getColumnType(columnIndex)))) {
            throw new ColumnFormatException("Incorrect value: '" + value + "' format, expected "
                    + table.getColumnType(columnIndex) + " , but was " + value.getClass());
        }
        list.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        return list.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Integer.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Integer) list.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Long.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Long) list.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Byte.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Byte) list.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Float.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Float) list.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Double.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Double) list.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!Boolean.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (Boolean) list.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
        }
        if (!String.class.equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Incorrect value format");
        }
        return (String) list.get(columnIndex);
    }
}