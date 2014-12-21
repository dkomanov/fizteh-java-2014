package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class Record implements Storeable {
    private List<Class<?>> valuesTypes;
    private List<Object> values;
    private int columnsAmount;

    public Record(List<Class<?>> valueTypes) {
        valuesTypes = valueTypes;
        columnsAmount = valueTypes.size();
        values = new ArrayList<>();
    }

    private <T> T getFormattedObjectAt(int columnIndex, Class<T> required) {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        Utility.checkCurrentColumnType(required, values.get(columnIndex));
        return (T) (values.get(columnIndex));
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        Object formatValue = Utility.compareColumnAndValueTypes(valuesTypes.get(columnIndex), value);
        //Object formatValue = Utility.checkValueColumnType(valuesTypes.get(columnIndex), value);
        values.add(columnIndex, formatValue);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        Utility.checkColumnIndex(columnIndex, columnsAmount);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getFormattedObjectAt(columnIndex, String.class);
    }
}
