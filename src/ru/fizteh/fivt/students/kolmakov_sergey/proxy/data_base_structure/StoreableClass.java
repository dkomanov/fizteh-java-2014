package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.*;

public class StoreableClass implements Storeable {

    private List<Object> values;
    private List<Class<?>> columnTypes;
    private static final Set<Class<?>> VALID_TYPES;
    static {
        VALID_TYPES = new HashSet<>();
        VALID_TYPES.add(Integer.class);
        VALID_TYPES.add(Long.class);
        VALID_TYPES.add(Byte.class);
        VALID_TYPES.add(Double.class);
        VALID_TYPES.add(Float.class);
        VALID_TYPES.add(Boolean.class);
        VALID_TYPES.add(String.class);
    }

    public StoreableClass(Table table) {
        List<Class<?>> newColumnTypes = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < table.getColumnsCount(); ++currentIndex) {
            newColumnTypes.add(table.getColumnType(currentIndex));
        }
        for (Class<?> type : newColumnTypes) {
            if (!VALID_TYPES.contains(type)) {
                throw new ColumnFormatException("Invalid column type");
            }
        }
        columnTypes = newColumnTypes;
        values = new ArrayList<>();
        for (int i = 0; i < columnTypes.size(); i++) {
            values.add(null);
        }
    }

    public StoreableClass(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        this(table);
        if (values.size() != this.values.size()) {
            throw new IndexOutOfBoundsException("Can't construct storeable: table signature size != initializer size");
        } else {
            for (int i = 0; i < values.size() && i < this.columnTypes.size(); ++i) {
                if (values.get(i).getClass() != this.columnTypes.get(i)) {
                    throw new ColumnFormatException("Creating storeable: table type doesn't match initializer type");
                } else {
                    this.values.set(i, values.get(i));
                }
            }
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value != null) {
            checkFormat(columnIndex, value.getClass());
        }
        values.set(columnIndex, value);
    }

    private <T> T getTypedAt(int columnIndex, Class<T> clazz) {
        checkBounds(columnIndex);
        checkFormat(columnIndex, clazz);
        return (T) values.get(columnIndex);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTypedAt(columnIndex, String.class);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof StoreableClass)) {
            return  false;
        } else {
            StoreableClass storeableClass = (StoreableClass) o;
            return storeableClass.values.equals(values) && storeableClass.columnTypes.equals(columnTypes);
        }
    }

    @Override
    public int hashCode() {
        return values.hashCode() + columnTypes.hashCode();
    }

    private void checkBounds(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Invalid column index");
        }
    }

    private void checkFormat(int columnIndex, Class<?> type) throws ColumnFormatException {
        if (!columnTypes.get(columnIndex).equals(type)) {
            throw new ColumnFormatException("Invalid column format");
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getClass().getSimpleName());
        stringBuilder.append("[");
        for (Object object : values) {
            if (object == null) {
                stringBuilder.append(",");
            } else {
                stringBuilder.append(object.toString());
                stringBuilder.append(",");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
