package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.util.List;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CStoreable implements Storeable {
    private Object[] values;

    public CStoreable(List<Object> listValues) {
        values = listValues.toArray();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value.getClass() != values[columnIndex].getClass()) {
            throw new ColumnFormatException("Invalid column format: expected "
                    + values[columnIndex].getClass().getName() + ", received " + value.getClass().getName());
        }
        values[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Not an instance of Integer");
        }
        return (Integer) values[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("Not an instance of Long");
        }
        return (Long) values[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("Not an instance of Byte");
        }
        return (Byte) values[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("Not an instance of Float");
        }
        return (Float) values[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("Not an instance of Double");
        }
        return (Double) values[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("Not an instance of Boolean");
        }
        return (Boolean) values[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!(values[columnIndex] instanceof String)) {
            throw new ColumnFormatException("Not an instance of String");
        }
        return (String) values[columnIndex];
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getClass().getSimpleName() + "[");
        for (int i = 0; i < values.length; i++) {
            stringBuilder.append(values[i] == null ? "" : values[i].toString());
            if (i < values.length - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
