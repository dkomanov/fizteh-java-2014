package ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.TypeTransformer;

import java.util.ArrayList;
import java.util.List;

public class MyTableRaw implements Storeable {
    private List<Object> values = new ArrayList<>();
    private List<Class<?>> types = new ArrayList<>();

    public MyTableRaw(List<Class<?>> types) {
        for (Class<?> type: types) {
            this.types.add(type);
            values.add(null);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        if (value != null) {
            checkType(columnIndex, value.getClass());
        }
        values.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        return values.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Integer.class);
        return (Integer) getColumnAt(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Long.class);
        return (Long) getColumnAt(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Byte.class);
        return (Byte) getColumnAt(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Float.class);
        return (Float) getColumnAt(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Double.class);
        return (Double) getColumnAt(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, Boolean.class);
        return (Boolean) getColumnAt(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkType(columnIndex, String.class);
        return (String) getColumnAt(columnIndex);
    }

    private void checkColumnIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException("there is no index " + columnIndex
                    + " (correct indexes from 0 to  " + (types.size() - 1) + ")");
        }
    }

    private void checkType(int columnIndex, Class<?> type) throws ColumnFormatException {
        if (!type.equals(types.get(columnIndex))) {
            throw new ColumnFormatException("wrong type (column index " + columnIndex + ": "
                    + "table type - " + TypeTransformer.getStringByType(types.get(columnIndex)) + ", "
                    + "given type - " + TypeTransformer.getStringByType(type) + ")");
        }
    }
}
