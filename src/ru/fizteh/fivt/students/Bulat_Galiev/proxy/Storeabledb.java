package ru.fizteh.fivt.students.Bulat_Galiev.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by user1 on 09.11.2014.
 */
public class Storeabledb implements Storeable {
    private Object[] columnValues;

    public Storeabledb(List<Object> values) {
        columnValues = values.toArray();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (value == null) {
            columnValues[columnIndex] = null;
        }
        if (value.getClass() != columnValues[columnIndex].getClass()) {
            throw new ColumnFormatException("Invalid column types: expected - "
                    + columnValues[columnIndex].getClass().getName()
                    + ", got - " + value.getClass().getName());

        }
        columnValues[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return columnValues[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        return getClassAt(columnIndex, String.class);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[");

        String[] stringObjects = new String[columnValues.length];
        for (int i = 0; i < columnValues.length; ++i) {
            stringObjects[i] = columnValues == null ? columnValues[i]
                    .toString() : "";
        }
        sb.append(String.join(",", stringObjects));
        sb.append("]");
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getClassAt(int columnIndex, Class<T> singleClass) {
        if (!singleClass.isInstance(columnValues[columnIndex])) {
            throw new ColumnFormatException("Element is not " + singleClass.getSimpleName());
        }
        return (T) columnValues[columnIndex];
    }
    
}
