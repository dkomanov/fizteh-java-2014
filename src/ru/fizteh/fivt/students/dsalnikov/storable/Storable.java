package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class Storable implements Storeable {
    private List<Class<?>> types;
    private List<Object> objectList;

    public Storable(Table t, List<?> values) {
        types = new ArrayList<>();
        objectList = new ArrayList<>();
        if (t.getColumnsCount() != t.size()) {
            throw new IndexOutOfBoundsException("storeable row: "
                    + "size of value-list not equals to amount of columns in table");
        }

        for (int i = 0; i < types.size(); ++i) {
            if (values.get(i) != null) {
                if (t.getColumnType(i) != values.get(i).getClass()) {
                    throw new ColumnFormatException("storeable row: "
                            + "types in value-list and in table's columns conflict");
                }
            }
            types.add(t.getColumnType(i));
            objectList.add(types.get(i));
        }
    }


    public Storable(Table table) {
        types = new ArrayList<>();
        objectList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            objectList.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndexPositionAndTypeMatch(columnIndex, value);
        objectList.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndexPosition(columnIndex);
        return objectList.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Integer) getOnjectAtIndex(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) getOnjectAtIndex(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) getOnjectAtIndex(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Float) getOnjectAtIndex(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) getOnjectAtIndex(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) getOnjectAtIndex(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) getOnjectAtIndex(columnIndex, String.class);
    }

    private Object getOnjectAtIndex(int index, Class<?> requiredObject) {
        checkIndexPositionAndTypeMatch(index, requiredObject);
        return objectList.get(index);
    }

    private void checkIndexPosition(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex > objectList.size() - 1) {
            throw new IndexOutOfBoundsException("storable index out of bounds exception");
        }
    }

    private void checkTypeMatch(int columnIndex, Object value) throws ColumnFormatException {
        if (value != null) {
            boolean b = !value.getClass().equals(types.get(columnIndex));
            Class t = types.get(columnIndex);
            String a = value.toString();
            boolean r = t.toString().equals(a);
            Class<?> clazz = types.get(columnIndex);
            if (!value.getClass().equals(clazz) && !value.toString().equals(clazz.toString())) {
                throw new ColumnFormatException("");
            }
        }
    }

    private void checkIndexPositionAndTypeMatch(int columnIndex, Object value) {
        checkIndexPosition(columnIndex);
        checkTypeMatch(columnIndex, value);
    }
}
