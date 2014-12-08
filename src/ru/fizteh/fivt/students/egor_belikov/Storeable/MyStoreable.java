package ru.fizteh.fivt.students.egor_belikov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.*;

public class MyStoreable implements Storeable {
    public List<Class<?>> typesList;
    private List<Object> currentValues;
    protected int columnsNum;

    public MyStoreable(List<Object> newValues, List<Class<?>> newTypes) {
        currentValues = new ArrayList<>(newValues);
        columnsNum = currentValues.size();
        typesList = new ArrayList<>(newTypes);
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (value.getClass() != typesList.get(columnIndex)) {
            throw new ColumnFormatException("Invalid column format: expected "
                    + currentValues.get(columnIndex).getClass().getName() + ", got " + value.getClass().getName());
        }
        currentValues.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        return currentValues.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Integer)) {
            throw new ColumnFormatException("Column is not Integer");
        }
        return (Integer) currentValues.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Long)) {
            throw new ColumnFormatException("Column is not Long");
        }
        return (Long) currentValues.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Byte)) {
            throw new ColumnFormatException("Column is not Byte");
        }
        return (Byte) currentValues.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Float)) {
            throw new ColumnFormatException("Column is not Float");
        }
        return (Float) currentValues.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Double)) {
            throw new ColumnFormatException("Column is not Double");
        }
        return (Double) currentValues.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof Boolean)) {
            throw new ColumnFormatException("Column is not Boolean");
        }
        return (Boolean) currentValues.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex > columnsNum) {
            throw new IndexOutOfBoundsException("This column does not exist");
        }
        if (!(currentValues.get(columnIndex) instanceof String)) {
            throw new ColumnFormatException("Column is not String");
        }
        return (String) currentValues.get(columnIndex);
    }
}
