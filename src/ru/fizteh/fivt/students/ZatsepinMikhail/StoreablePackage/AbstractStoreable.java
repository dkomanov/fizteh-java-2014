package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class AbstractStoreable implements Storeable {
    private Object[] values;

    public AbstractStoreable(Object[] newValues) {
        values = newValues;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != value.getClass()) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
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
        if (values[columnIndex].getClass() != Integer.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Integer.class);
        }
        return (Integer) values[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != Long.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Long.class);
        }
        return (Long) values[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != Byte.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Byte.class);
        }
        return (Byte) values[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != Float.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Float.class);
        }
        return (Float) values[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != Double.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Double.class);
        }
        return (Double) values[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != Boolean.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + Boolean.class);
        }
        return (Boolean) values[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values[columnIndex].getClass() != String.class) {
            throw new ColumnFormatException("expected:" + values[columnIndex].getClass()
                    + " , but got:" + String.class);
        }
        return (String) values[columnIndex];
    }
}
