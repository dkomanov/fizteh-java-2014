package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by moskupols on 03.12.14.
 */
class StoreableImpl implements Storeable {

    private final List<StoreableAtomType> signature;
    private List<Object> values;

    StoreableImpl(List<StoreableAtomType> signature) {
        this.signature = signature;
        this.values = new ArrayList<>(Collections.nCopies(signature.size(), null));
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && !signature.get(columnIndex).boxedClass.isInstance(value)) {
            throw new ColumnFormatException(signature.get(columnIndex).printedName + " expected");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    protected void checkGetterType(Class<?> type, int columnIndex) {
        final Class<?> expected = signature.get(columnIndex).boxedClass;
        if (!expected.equals(type)) {
            throw new ColumnFormatException(
                    String.format(
                            "Type of column %d is %s, not %s",
                            columnIndex,
                            expected.getSimpleName(),
                            type.getSimpleName()));
        }
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Integer.class, columnIndex);
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Long.class, columnIndex);
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Byte.class, columnIndex);
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Float.class, columnIndex);
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Double.class, columnIndex);
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(Boolean.class, columnIndex);
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetterType(String.class, columnIndex);
        return (String) values.get(columnIndex);
    }
}
