package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.Objects;

public class StoreableImpl implements Storeable {
    private final Object[] values;

    private Table host;

    /**
     * Creates a new instance of Storeable with null values as default.
     * @param host
     *         Host table.
     */
    StoreableImpl(Table host) {
        this.host = host;
        this.values = new Object[host.getColumnsCount()];
    }

    Table getHost() {
        return host;
    }

    private void ensureMatchColumnType(int columnIndex, Class<?> clazz) throws ColumnFormatException {
        Class<?> columnType = host.getColumnType(columnIndex);
        if (!columnType.equals(clazz)) {
            throw new ColumnFormatException(
                    String.format(
                            "wrong type (Table '%s', col %d: Expected instance of %s, but got %s)",
                            host.getName(),
                            columnIndex,
                            columnType.getSimpleName(),
                            clazz.getSimpleName()));
        }
    }

    private void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= values.length) {
            throw new IndexOutOfBoundsException(
                    "Column index expected to be from zero to {columns.length - 1}, but got: " + columnIndex);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);

        if (value != null) {
            ensureMatchColumnType(columnIndex, value.getClass());
        }
        values[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return values[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedValue(columnIndex, String.class);
    }

    private <T> T getTypedValue(int columnIndex, Class<T> clazz)
            throws IndexOutOfBoundsException, ColumnFormatException {
        checkIndex(columnIndex);
        ensureMatchColumnType(columnIndex, clazz);
        return (T) values[columnIndex];
    }

    @Override
    public int hashCode() {
        return host.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoreableImpl)) {
            return false;
        }
        StoreableImpl storeable = (StoreableImpl) obj;

        if (host != storeable.host) {
            return false;
        }

        if (host.getColumnsCount() != storeable.host.getColumnsCount()) {
            return false;
        }

        for (int col = 0, colsCount = host.getColumnsCount(); col < colsCount; col++) {
            if (!host.getColumnType(col).equals(storeable.host.getColumnType(col))) {
                return false;
            }
        }

        for (int col = 0, colsCount = host.getColumnsCount(); col < colsCount; col++) {
            if (!Objects.equals(values[col], storeable.values[col])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (host instanceof StoreableTableImpl) {
            return ((StoreableTableImpl) host).getProvider().serialize(host, this);
        }
        return super.toString();
    }
}
