package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Created by andrew on 07.11.14.
 */
public class TableRow implements Storeable {
    private TableSignature tableSignature;
    private Object[] row;

    public TableRow(TableSignature tableSignature) {
        this.tableSignature = tableSignature;
        row = new Object[tableSignature.size()];
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            assertClassesEqualityAtIndex(value.getClass(), columnIndex);
        }
        row[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return row[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Integer.class, columnIndex);
        return (Integer) row[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Long.class, columnIndex);
        return (Long) row[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Byte.class, columnIndex);
        return (Byte) row[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Float.class, columnIndex);
        return (Float) row[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Double.class, columnIndex);
        return (Double) row[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Boolean.class, columnIndex);
        return (Boolean) row[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(String.class, columnIndex);
        return (String) row[columnIndex];
    }

    private void assertClassesEqualityAtIndex(Class<?> desired, int columnIndex) throws ColumnFormatException {
        Class<?> columnClass = tableSignature.getClassAt(columnIndex);
        if (desired != columnClass) {
            throw new ColumnFormatException(
                    String.format("column class at: %d is %s, desired: %s", columnIndex,
                            columnClass.getSimpleName(), desired.getSimpleName()));
        }
    }
}
