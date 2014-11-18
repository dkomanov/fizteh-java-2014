package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by drack3800 on 16.11.2014.
 */
public class TableRow implements Storeable {
    private List<Object> values;

    public TableRow(final List<Object> values) {
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value == null) {
            values.set(columnIndex, null);
        } else if (value.getClass().equals(values.get(columnIndex).getClass())) {
            values.set(columnIndex, value);
        } else {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getTat(columnIndex, String.class);
    }

    private <T> T getTat(int columnIndex, Class<T> type) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        Object val = values.get(columnIndex);
        if (val == null) {
            return null;
        } else if (!(val.getClass().equals(type))) {
            throw new ColumnFormatException();
        } else {
            return (T) val;
        }
    }

    private void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= values.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
}
