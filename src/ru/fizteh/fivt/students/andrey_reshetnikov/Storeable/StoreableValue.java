package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class StoreableValue implements Storeable {

    private List<Object> values;
    private List<Class<?>> types;

    protected StoreableValue(List<Class<?>> passedTypes) {
        types = passedTypes;
        values = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            values.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException(value.toString() + " must be a " + types.get(columnIndex).getSimpleName()
                    + " found " + value.getClass() + " instead.");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Integer.class) {
            throw new ColumnFormatException();
        }
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Long.class) {
            throw new ColumnFormatException();
        }
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Byte.class) {
            throw new ColumnFormatException();
        }
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Float.class) {
            throw new ColumnFormatException();
        }
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Double.class) {
            throw new ColumnFormatException();
        }
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Boolean.class) {
            throw new ColumnFormatException();
        }
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != String.class) {
            throw new ColumnFormatException();
        }
        return (String) values.get(columnIndex);
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    public List<Object> getValues() {
        return values;
    }
}
