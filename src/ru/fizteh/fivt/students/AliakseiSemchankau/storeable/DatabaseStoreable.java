package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by Aliaksei Semchankau on 19.11.2014.
 */
public class DatabaseStoreable implements Storeable {

    Object[] column;

    public DatabaseStoreable(List<Object> columnToTransform) {
        column = columnToTransform.toArray();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }

        if (value == null) {
            column[columnIndex] = null;
            return;
        }
        if (value.getClass() != column[columnIndex].getClass()) {
            throw new ColumnFormatException("value class is " + value.getClass() + ", column class is "
                    + column[columnIndex].getClass());
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        return column[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Integer");
        }
        return (Integer) column[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Long)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Long");
        }
        return (Long) column[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Byte");
        }
        return (Byte) column[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Float)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Float");
        }
        return (Float) column[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Double)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Double");
        }
        return (Double) column[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of Boolean");
        }
        return (Boolean) column[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.length) {
            throw new IndexOutOfBoundsException("columnIndex = " + columnIndex + ", bound is 0-" + column.length);
        }
        if (!(column[columnIndex] instanceof String)) {
            throw new ColumnFormatException(column[columnIndex].getClass().toString() + "is not instanse of String");
        }
        return (String) column[columnIndex];
    }

}
