package ru.fizteh.fivt.students.gudkov394.Storable.src;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;


/**
 * Created by kagudkov on 07.11.14.
 */
public class tableContents implements Storeable {
    private Object[] fields;

    public tableContents(List<Object> values) {
        fields = values.toArray();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (value.getClass() != fields[columnIndex].getClass()) {
            throw new ColumnFormatException("Invalid column format. I expected " + fields[columnIndex].getClass()
                    + "instead" + value.getClass());
        }
        fields[columnIndex] = value;

    }

    private void goodColumnIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= fields.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        return fields[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("It is not Integer");
        }
        return (Integer) fields[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("It is not Long");
        }
        return (Long) fields[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("It is not Byte");
        }
        return (Byte) fields[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("It is not Float");
        }
        return (Float) fields[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("It is not Double");
        }
        return (Double) fields[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("It is not Boolean");
        }
        return (Boolean) fields[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields[columnIndex] instanceof String)) {
            throw new ColumnFormatException("It is not String");
        }
        return (String) fields[columnIndex];
    }
}
