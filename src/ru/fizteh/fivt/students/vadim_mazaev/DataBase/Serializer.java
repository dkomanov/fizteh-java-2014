package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class Serializer implements Storeable {
    List<Class<?>> structure;
    List<Object> storage;
    
    public Serializer(List<Class<?>> structure) {
        storage = new ArrayList<>(structure.size());
        this.structure = structure;
    }
    
    private void checkIndexInBounds(int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= structure.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    private void checkColumnFormat(int columnIndex, Class<?> classType) {
        if (structure.get(columnIndex) != classType) {
            throw new ColumnFormatException("Expected '"
                    + structure.get(columnIndex).getSimpleName() + "' but found '"
                    + classType.getSimpleName() + "'");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
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

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Integer.class);
        if (value != null) {
            return (Integer) value;
        } else {
            return null;
        }
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Long.class);
        if (value != null) {
            return (Long) value;
        } else {
            return null;
        }
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Byte.class);
        if (value != null) {
            return (Byte) value;
        } else {
            return null;
        }
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Float.class);
        if (value != null) {
            return (Float) value;
        } else {
            return null;
        }
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Double.class);
        if (value != null) {
            return (Double) value;
        } else {
            return null;
        }
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, Boolean.class);
        if (value != null) {
            return (Boolean) value;
        } else {
            return null;
        }
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, String.class);
        if (value != null) {
            return (String) value;
        } else {
            return null;
        }
    }

}
