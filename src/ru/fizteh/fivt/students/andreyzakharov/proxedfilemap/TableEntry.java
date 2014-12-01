package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

public class TableEntry implements Storeable {
    private Object[] fields;

    public TableEntry(List<Object> values) {
        fields = values.toArray();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value.getClass() != fields[columnIndex].getClass()) {
            throw new ColumnFormatException("Invalid column format: expected "
                    + fields[columnIndex].getClass().getName() + ", got " + value.getClass().getName());
        }
        fields[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return fields[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Column is not Integer");
        }
        return (Integer) fields[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("Column is not Long");
        }
        return (Long) fields[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("Column is not Byte");
        }
        return (Byte) fields[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("Column is not Float");
        }
        return (Float) fields[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("Column is not Double");
        }
        return (Double) fields[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("Column is not Boolean");
        }
        return (Boolean) fields[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(fields[columnIndex] instanceof String)) {
            throw new ColumnFormatException("Column is not String");
        }
        return (String) fields[columnIndex];
    }
}
