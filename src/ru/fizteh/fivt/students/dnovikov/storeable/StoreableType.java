package ru.fizteh.fivt.students.dnovikov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;


public class StoreableType implements Storeable {

    private List<Class<?>> types;
    private Object[] data;


    public StoreableType(List<Class<?>> types) {
        this.types = new ArrayList<>(types);
        data = new Object[types.size()];
    }

    private void checkIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= data.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkFormat(int columnIndex, Class<?> type) {
        if (types.get(columnIndex) != type) {
            throw new ColumnFormatException("expected format: '" + types.get(columnIndex)
                    + "', actual format: '" + type.getSimpleName() + "'");
        }
    }

    public <T> T getColumnType(int columnIndex, Class<T> type) {
        checkIndex(columnIndex);
        Object value = getColumnAt(columnIndex);
        checkFormat(columnIndex, type);
        if (value != null) {
            return type.cast(value);
        } else {
            return null;
        }
    }

    public int getNumberOfColumns() {
        return types.size();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkFormat(columnIndex, value.getClass());
        }
        data[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return data[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getColumnType(columnIndex, String.class);
    }
}
