package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 24.11.2014.
 */
public class MyStoreable implements Storeable {

    List<Object> values;
    List<Class<?>> types;

    protected MyStoreable(List<Class<?>> passedTypes) {
        types = passedTypes;
        values = new ArrayList<>();
        for (int i = 0; i < types.size(); ++i) {
            values.add(null);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException(value.toString() + " must be a " + types.get(columnIndex).getSimpleName()
                    + ", but found " + value.getClass() + " instead");
        }
        values.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Integer.class) {
            throw new ColumnFormatException();
        }
        return (Integer) values.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Long.class) {
            throw new ColumnFormatException();
        }
        return (Long) values.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Byte.class) {
            throw new ColumnFormatException();
        }
        return (Byte) values.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Float.class) {
            throw new ColumnFormatException();
        }
        return (Float) values.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Double.class) {
            throw new ColumnFormatException();
        }
        return (Double) values.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.get(columnIndex) != Boolean.class) {
            throw new ColumnFormatException();
        }
        return (Boolean) values.get(columnIndex);
    }

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
