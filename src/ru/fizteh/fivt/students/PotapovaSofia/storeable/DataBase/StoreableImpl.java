package ru.fizteh.fivt.students.PotapovaSofia.storeable.DataBase;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class StoreableImpl implements Storeable{
    private List<Class<?>> signature;
    private List<Object> storage;

    public StoreableImpl(List<Class<?>> signature) {
        storage = new ArrayList<>(signature.size());
        this.signature = new ArrayList<>(signature);
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (value != null) {
            checkColumnFormat(columnIndex, value.getClass());
        }
        storage.add(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException();
        }
        return storage.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastType(columnIndex, String.class);
    }

    private void checkColumnFormat(int columnIndex, Class<?> classType) {
        if (signature.get(columnIndex) != classType) {
            throw new ColumnFormatException("Expected: " + signature.get(columnIndex).getSimpleName()
                    + "; found: " + classType.getSimpleName());
        }
    }

    private <T> T getCastType(int columnIndex, Class<T> type) {
        Object value = getColumnAt(columnIndex);
        checkColumnFormat(columnIndex, type);
        if (value != null) {
            return type.cast(value);
        } else {
            return null;
        }
    }
}
