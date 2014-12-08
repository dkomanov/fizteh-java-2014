package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.*;

/**
 * Created by Alex on 22.11.14.
 */
public class StoreableEntry implements Storeable {

    private Set<Class<?>> typeSet;
    private List<Class<?>> signatyre;
    private List<Object> objectList;

    private void init() {
        typeSet = new HashSet<Class<?>>();
        typeSet.add(Integer.class);
        typeSet.add(Long.class);
        typeSet.add(Byte.class);
        typeSet.add(Float.class);
        typeSet.add(Double.class);
        typeSet.add(Boolean.class);
        typeSet.add(String.class);
    }

    public StoreableEntry(List<Class<?>> sign) {
        init();
        signatyre = sign;
        for (Class<?> type : signatyre) {
            if (!typeSet.contains(type)) {
                throw new ColumnFormatException("Invalid type");
            }
        }
        objectList = new ArrayList<>();
        for (int i = 0; i < signatyre.size(); i++) {
            objectList.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (value != null) {
            if (!signatyre.get(columnIndex).equals(value.getClass())) {
                throw new ColumnFormatException("Invalid column");
            }
        }
        objectList.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return objectList.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Integer) objectList.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Long) objectList.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Byte) objectList.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Float) objectList.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Double) objectList.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (Boolean) objectList.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= signatyre.size()) {
            throw new IndexOutOfBoundsException("Invalid columnIndex");
        }
        if (!signatyre.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Invalid column");
        }
        return (String) objectList.get(columnIndex);
    }

    public Class<?> getColumnType(int index) throws IndexOutOfBoundsException {
        return signatyre.get(index);
    }

    public List<Object> getObjectList() {
        return objectList;
    }
}
