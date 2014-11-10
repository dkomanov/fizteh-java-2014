package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class StoreableValue implements Storeable {

    List<Object> values;
    List<Class<?>> types;

    protected StoreableValue(List<Class<?>> passedTypes) {
        types = passedTypes;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException();
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
