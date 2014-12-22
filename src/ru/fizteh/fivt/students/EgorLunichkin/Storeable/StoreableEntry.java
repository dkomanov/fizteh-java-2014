package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;
import java.util.List;

public class StoreableEntry implements Storeable {
    public StoreableEntry(List<Class<?>> givenTypes) {
        types = givenTypes;
        values = new ArrayList<>();
        for (int i = 0; i < types.size(); ++i) {
            values.add(null);
        }
    }

    public List<Object> values;
    public List<Class<?>> types;

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException {
        if (value != null && types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException("Incorrect column format");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Integer.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Long.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Byte.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Float.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Double.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != Boolean.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException {
        if (types.get(columnIndex) != String.class) {
            throw new ColumnFormatException("Incorrect column format");
        }
        return (String) values.get(columnIndex);
    }
}
