package parallel.util;

import java.util.ArrayList;
import java.util.List;

import storeable.structured.*;

public class MyStoreable implements Storeable {
    public List<Object> columns;
    List<Class<?>> types;

    public MyStoreable(List<Class<?>> types) {
        this.types = types;
        columns = new ArrayList<Object>();
        for (int i = 0; i < types.size(); i++) {
            columns.add(null);
        }
    }

    private void checkIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columns.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException("column index is out of bound");
        }
    }

    private void checkFormat(int columnIndex, Class<?> type) throws ColumnFormatException {
        if (!types.get(columnIndex).equals(type)) {
            throw new ColumnFormatException("wrong type (giving " + type
                    + " instead of " + types.get(columnIndex) + ")");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) 
                            throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkFormat(columnIndex, value.getClass());
        }

        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return columns.get(columnIndex);
    }

    public Class<?> getTypeAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return types.get(columnIndex);
    }

    public int getSize() {
        return types.size();
    }

    @Override
    public Integer getIntAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Integer.class);
        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Long.class);
        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Byte.class);
        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Float.class);
        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Double.class);
        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, Boolean.class);
        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) 
                        throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormat(columnIndex, String.class);
        return (String) columns.get(columnIndex);
    }
}
