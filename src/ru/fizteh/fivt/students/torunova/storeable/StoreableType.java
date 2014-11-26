package ru.fizteh.fivt.students.torunova.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.util.Arrays;

/**
 * Created by nastya on 16.11.14.
 */
public class StoreableType implements ru.fizteh.fivt.storage.structured.Storeable {
    private Class<?>[] types;
    private Object[] values;
    public StoreableType(Class<?>... newTypes) {
        types = newTypes;
        values = new Object[types.length];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoreableType)) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            //TODO: fix equals.
            if (values[i] != null
                    && !values[i].equals(((StoreableType) obj).getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        if (value != null) {
            checkColumnFormat(columnIndex, value.getClass());
        }
        values[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        return values[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Integer.class);
        return (Integer) values[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Long.class);
        return (Long) values[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Byte.class);
        return (Byte) values[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Float.class);
        return (Float) values[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Double.class);
        return (Double) values[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Boolean.class);
        return (Boolean) values[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, String.class);
        return (String) values[columnIndex];
    }
    public int getNumberOfColumns() {
        return types.length;
    }
    public Class<?> getColumnType(int columnIndex) {
        return types[columnIndex];
    }
    private void checkColumnFormat(int columnIndex, Class<?> expectedClass) throws ColumnFormatException {
        if (!types[columnIndex].equals(expectedClass)) {
            throw new ColumnFormatException("Wrong type of value in column " + columnIndex);
        }
    }
    private void checkColumnIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= values.length) {
            throw new IndexOutOfBoundsException();
        }
    }

}
