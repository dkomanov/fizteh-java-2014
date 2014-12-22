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

    private Object assertColumnType(int columnIndex, Class<?> type) {
        if (types.get(columnIndex) == type) {
            return values.get(columnIndex);
        } else {
            throw new ColumnFormatException("This column type is not " + type.toString());
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
        return (Integer) assertColumnType(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) assertColumnType(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) assertColumnType(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Float) assertColumnType(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) assertColumnType(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) assertColumnType(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) assertColumnType(columnIndex, String.class);
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    public List<Object> getValues() {
        return values;
    }
}
