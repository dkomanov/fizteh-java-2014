package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;
import java.util.List;

public class StoreableEntry implements Storeable {
    public StoreableEntry(List<Class<?>> givenTypes) {
        types = givenTypes;
        values = new ArrayList<>(types.size());
    }

    public List<Object> values;
    public List<Class<?>> types;

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {

    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }
}
