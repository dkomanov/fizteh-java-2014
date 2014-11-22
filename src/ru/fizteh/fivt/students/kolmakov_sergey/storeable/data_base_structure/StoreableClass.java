package ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_structure;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.*;

public class StoreableClass implements Storeable {

    private List<Object> values;
    private static Set<Class<?>> validTypes;
    private List<Class<?>> columnTypes;

    public StoreableClass(Table table) {
        initializeValidTypesList();
        List<Class<?>> newColumnTypes = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < table.getColumnsCount(); ++currentIndex) {
            newColumnTypes.add(table.getColumnType(currentIndex));
        }
        for (Class<?> type : newColumnTypes) {
            if (!validTypes.contains(type)) {
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
            for (int i = 0; i < values.size(); ++i) {
                if (values.getClass() != this.values.getClass()) {
                    throw new ColumnFormatException("Creating storeable: table type doesn't match initializer type");
                } else {
                    this.values.set(i, values.get(i));
                }
            }
        }
    }

    private void initializeValidTypesList() {
        if (validTypes == null) {
            validTypes = new HashSet<>();
            validTypes.add(Integer.class);
            validTypes.add(Long.class);
            validTypes.add(Byte.class);
            validTypes.add(Double.class);
            validTypes.add(Float.class);
            validTypes.add(Boolean.class);
            validTypes.add(String.class);
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

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Integer.class);
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Long.class);
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Byte.class);
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Float.class);
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Double.class);
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, Boolean.class);
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkFormat(columnIndex, String.class);
        return (String) values.get(columnIndex);
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
}
