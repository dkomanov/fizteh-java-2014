package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий on 25.11.2014.
 */
public class CurrentStoreable implements Storeable {

    private final List<Object> values = new ArrayList<>();
    private final List<Class<?>> types;

    public List<Object> getValues() {
        return values;
    }

    public CurrentStoreable(List<Class<?>> types) {
        this.types = types;
        for (int i = 0; i < types.size(); i++) {
            values.add(null);
        }
    }

    private void checkFormat(int columnIndex, Class<?> type) throws ColumnFormatException {
        if (!types.get(columnIndex).equals(type)) {
            throw new ColumnFormatException("Invalid column format");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException(
                    value.toString() + " must be a " + types.get(columnIndex).getSimpleName()
                        + " not a " + value.getClass());
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Integer.class);
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Long.class);
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Byte.class);
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Float.class);
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Double.class);
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, Boolean.class);
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkFormat(columnIndex, String.class);
        return (String) values.get(columnIndex);
    }
}
