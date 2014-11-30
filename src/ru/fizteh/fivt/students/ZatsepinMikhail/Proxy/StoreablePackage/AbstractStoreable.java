package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class AbstractStoreable implements Storeable {
    private Object[] values;
    private Table parentTable;

    public AbstractStoreable(Object[] newValues, Table newParent) {
        values = newValues;
        parentTable = newParent;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value == null) {
            values[columnIndex] = value;
            return;
        }
        if (!parentTable.getColumnType(columnIndex).equals(value.getClass())) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                + " , but got:" + value.getClass());
        } else {
            values[columnIndex] = value;
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Integer.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Integer.class);
        }
        return (Integer) values[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Long.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Long.class);
        }
        return (Long) values[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Byte.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Byte.class);
        }
        return (Byte) values[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Float.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Float.class);
        }
        return (Float) values[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Double.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Double.class);
        }
        return (Double) values[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(Boolean.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + Boolean.class);
        }
        return (Boolean) values[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!values[columnIndex].getClass().equals(String.class)) {
            throw new ColumnFormatException("expected:" + parentTable.getColumnType(columnIndex)
                    + " , but got:" + String.class);
        }
        return (String) values[columnIndex];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getClass().getSimpleName() + "[");
        for (int i = 0; i < values.length; ++i) {
            if (i > 0) {
                str.append(",");
            }
            if (values[i] != null) {
                str.append(values[i]);
            }
        }
        str.append(']');
        return str.toString();
    }
}
