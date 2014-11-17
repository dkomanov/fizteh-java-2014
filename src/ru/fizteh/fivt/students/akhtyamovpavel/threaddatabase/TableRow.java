package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by user1 on 09.11.2014.
 */
public class TableRow implements Storeable {
    private Object[] columnValues;


    public TableRow(List<Object> values) {
        columnValues = values.toArray();
    }
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value == null) {
            columnValues[columnIndex] = null;
        }
        if (value.getClass() != columnValues[columnIndex].getClass()) {
            throw new ColumnFormatException("Invalid column types: expected - "
                    + columnValues[columnIndex].getClass().getName() + ", got - "
                    + value.getClass().getName());

        }
        columnValues[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return columnValues[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Element is not an Integer");
        }
        return (Integer) columnValues[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("Element is not a Long");
        }
        return (Long) columnValues[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("Element is not a Byte");
        }
        return (Byte) columnValues[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("Element is not a Float");
        }
        return (Float) columnValues[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("Element is not a Double");
        }
        return (Double) columnValues[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("Element is not a Boolean");
        }
        return (Boolean) columnValues[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(columnValues[columnIndex] instanceof String)) {
            throw new ColumnFormatException("Element is not a String");
        }
        return (String) columnValues[columnIndex];
    }
}
