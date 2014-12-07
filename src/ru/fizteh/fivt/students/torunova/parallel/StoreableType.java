package ru.fizteh.fivt.students.torunova.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.torunova.junit.*;

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
            if (values[i] != null
                    && !values[i].equals(((StoreableType) obj).getColumnAt(i))) {
                return false;
            }  else if (values[i] == null && ((StoreableType) obj).getColumnAt(i) != null) {
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
        return  getTypeAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return  getTypeAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return  getTypeAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return  getTypeAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return  getTypeAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, Boolean.class);
        return (Boolean) values[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return  getTypeAt(columnIndex, String.class);
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
    private <T> T getTypeAt(int columnIndex, Class<T> clazz) {
        checkColumnIndex(columnIndex);
        checkColumnFormat(columnIndex, clazz);
        return (T) values[columnIndex];
    }

}
