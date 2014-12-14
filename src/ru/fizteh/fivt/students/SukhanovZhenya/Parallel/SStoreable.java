package ru.fizteh.fivt.students.SukhanovZhenya.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import java.util.List;

public class SStoreable implements Storeable {

    private int size;
    private List<Class<?>> types;
    private List<Object> values;

    List<Object> getObjects() {
        return values;
    }

    List<Class<?>> getTypes() {
        return types;
    }

    public SStoreable(List<Object> objects, List<Class<?>> classes) throws ColumnFormatException {
        if (classes.size() != objects.size()) {
            throw new ColumnFormatException("Wrong type size (" + classes.size() + ", " + objects.size() + ")");
        }


        size = classes.size();
        types = classes;
        values = objects;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }

        if (!types.get(columnIndex).equals(value.getClass())) {
            throw new ColumnFormatException("Incorrect type");
        }

        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= size) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        if (values.get(columnIndex) == null) {
            return null;
        }
        return (String) values.get(columnIndex);
    }
}
