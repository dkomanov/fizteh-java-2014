package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class Serializer implements Storeable {
    List<Class<?>> structure;
    List<Object> storage;

    public Serializer(List<Class<?>> structure) {
        storage = new ArrayList<>(structure.size());
        this.structure = new ArrayList<>(structure);
    }

    public static void checkIndexInBounds(int maxIndex, int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= maxIndex) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + "expected index from 0 to " + maxIndex
                    + ", but found " + columnIndex);
        }
    }

    private void checkIndexInBounds(int columnIndex) throws IndexOutOfBoundsException {
        checkIndexInBounds(structure.size(), columnIndex);
    }

    private void checkColumnFormat(int columnIndex, Class<?> classType) {
        if (structure.get(columnIndex) != classType) {
            throw new ColumnFormatException("Expected '" + structure.get(columnIndex).getSimpleName() + "' but found '"
                    + classType.getSimpleName() + "'");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndexInBounds(columnIndex);
        if (value != null) {
            checkColumnFormat(columnIndex, value.getClass());
        }
        storage.add(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndexInBounds(columnIndex);
        return storage.get(columnIndex);
    }

    private <T> T checkAndGetType(int columnIndex, Class<T> type) {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, type);
        if (value != null) {
            return type.cast(value);
        } else {
            return null;
        }
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Byte.class);
        if (value != null) {
            return (Byte) value;
        } else {
            return null;
        }
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return checkAndGetType(columnIndex, String.class);
    }

    @Override
    public String toString() {
        String[] values = new String[storage.size()];
        int i = 0;
        for (Object value : storage) {
            if (value != null) {
                values[i] = value.toString();
            } else {
                values[i] = "";
            }
            i++;
        }
        return getClass().getSimpleName() + "[" + String.join(",", values) + "]";
    }
}
