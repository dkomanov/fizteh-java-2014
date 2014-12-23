package ru.fizteh.fivt.students.sautin1.telnet.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * One row of a StoreableTable.
 * Created by sautin1 on 12/9/14.
 */
public class TableRow implements Storeable {
    private List<Object> rowValues;
    private final List<Class<?>> valueTypes;

    public TableRow(List<Class<?>> valueTypes) {
        this.valueTypes = new ArrayList<>(valueTypes);
        rowValues = new ArrayList<>(valueTypes.size());
        for (int valueIndex = 0; valueIndex < valueTypes.size(); ++valueIndex) {
            rowValues.add(valueIndex, null);
        }
    }

    public TableRow(TableRow other) {
        this.valueTypes = new ArrayList<>(other.valueTypes);
        this.rowValues = new ArrayList<>(other.rowValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowValues, valueTypes);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof TableRow)) {
            return false;
        }
        if (!Objects.deepEquals(rowValues, ((TableRow) other).rowValues)) {
            return false;
        }
        if (!Objects.deepEquals(valueTypes, ((TableRow) other).valueTypes)) {
            return false;
        }
        return true;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            StoreableValidityChecker.checkClassAssignable(valueTypes.get(columnIndex), value.getClass());
        }
        rowValues.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return rowValues.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Integer.class, valueTypes.get(columnIndex));
        return (Integer) getColumnAt(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Long.class, valueTypes.get(columnIndex));
        return (Long) getColumnAt(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Byte.class, valueTypes.get(columnIndex));
        return (Byte) getColumnAt(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Float.class, valueTypes.get(columnIndex));
        return (Float) getColumnAt(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Double.class, valueTypes.get(columnIndex));
        return (Double) getColumnAt(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(Boolean.class, valueTypes.get(columnIndex));
        return (Boolean) getColumnAt(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValidityChecker.checkClassAssignable(String.class, valueTypes.get(columnIndex));
        return (String) getColumnAt(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");

        for (Object value : rowValues) {
            if (value != null) {
                builder.append(value);
            }

            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }
}
