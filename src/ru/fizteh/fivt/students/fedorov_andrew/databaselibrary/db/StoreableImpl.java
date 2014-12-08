package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONComplexObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of Storeable that can be put to the table it is assigned to as a value.<br/>
 * Not thread-safe.<br/>
 * Not bound to any table.
 */
@JSONComplexObject(wrapper = true)
public final class StoreableImpl implements Storeable {
    @JSONField
    private final Object[] values;

    private final List<Class<?>> types;

    /**
     * Creates a new instance of Storeable with null values as default.
     * @param host
     *         Host table.
     */
    StoreableImpl(Table host) {
        if (host instanceof StoreableTableImpl) {
            // Memory optimization.
            types = ((StoreableTableImpl) host).getColumnTypes();
        } else {
            types = new ArrayList<>(host.getColumnsCount());
            for (int i = 0; i < host.getColumnsCount(); i++) {
                types.add(host.getColumnType(i));
            }
        }
        this.values = new Object[host.getColumnsCount()];
    }

    List<Class<?>> getTypes() {
        return types;
    }

    private void ensureMatchColumnType(int columnIndex, Class<?> clazz) throws ColumnFormatException {
        Class<?> columnType = types.get(columnIndex);
        if (!columnType.equals(clazz)) {
            throw new ColumnFormatException(
                    String.format(
                            "wrong type (col %d: Expected instance of %s, but got %s)",
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
        return values.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoreableImpl)) {
            return false;
        }
        StoreableImpl storeable = (StoreableImpl) obj;

        if (values.length != storeable.values.length) {
            return false;
        }

        for (int col = 0; col < storeable.values.length; col++) {
            if (!Objects.equals(values[col], storeable.values[col])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(StoreableImpl.class.getSimpleName()).append('[');

        boolean comma = false;
        for (Object obj : values) {
            if (comma) {
                sb.append(',');
            }
            comma = true;
            if (obj != null) {
                sb.append(obj.toString());
            }
        }

        sb.append(']');
        return sb.toString();
    }
}
