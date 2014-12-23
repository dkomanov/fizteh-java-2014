package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.StoreableValue;

import java.util.List;
import java.util.Vector;

public class AdvancedStoreableValue implements Storeable {

    Storeable origin;

    protected AdvancedStoreableValue(Storeable passedOrigin) {
        origin = passedOrigin;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        origin.setColumnAt(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return origin.getColumnAt(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getIntAt(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getLongAt(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getByteAt(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getFloatAt(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getDoubleAt(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getBooleanAt(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getStringAt(columnIndex);
    }

    public Storeable getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        List<String> parsed = new Vector<>();
        for (Object value : ((StoreableValue) origin).getValues()) {
            if (value == null) {
                parsed.add("");
            } else {
                parsed.add(value.toString());
            }
        }
        return this.getClass().getSimpleName() + "[" + String.join(",", parsed) + "]";
    }
}
