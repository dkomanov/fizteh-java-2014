package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class TableEntry implements Storeable {
    List<Class<?>> signature;
    List<Object> storage;

    public TableEntry(List<Class<?>> signature) {
        this.signature = new ArrayList<>(signature);
        storage = new ArrayList<>(signature.size());
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value != null) {
            checkSignature(columnIndex, value.getClass());
        }
        storage.add(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return storage.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return getTemplate(columnIndex, String.class);
    }

    private void checkBounds(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkSignature(int columnIndex, Class<?> type) {
        Class<?> expectedType = signature.get(columnIndex);
        if (expectedType != type) {
            throw new ColumnFormatException(
                    "Expected: " + Serializer.TYPES_TO_NAMES.get(expectedType) + ", found: " + Serializer.TYPES_TO_NAMES
                            .get(type));
        }
    }

    private <T> T getTemplate(int columnIndex, Class<T> type) {
        checkSignature(columnIndex, type);
        Object value = storage.get(columnIndex);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Storeable object = (Storeable) obj;
        try {
            for (int i = 0; i < signature.size(); ++i) {
                if (!getColumnAt(i).equals(object.getColumnAt(i))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        try {
            object.getColumnAt(signature.size());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 42; // Let's start...
        for (Object entry : storage) {
            result += entry.hashCode();
        }

        return result;
    }
}
