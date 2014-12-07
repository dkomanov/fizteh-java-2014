package ru.fizteh.fivt.students.gudkov394.Storable.src;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;


/**
 * Created by kagudkov on 07.11.14.
 */
public class TableContents implements Storeable {
    private List<Object> fields;

    public List<Object> getFields() {
        return fields;
    }

    public TableContents(List<Object> values) {
        fields = values;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (value != null && value.getClass() != fields.get(columnIndex).getClass()) {
            throw new ColumnFormatException("Invalid column format. I expected " + fields.get(columnIndex).getClass()
                    + " instead " + value.getClass());
        }
        fields.set(columnIndex, value);

    }

    private void goodColumnIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= fields.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        return fields.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Integer)) {
            throw new ColumnFormatException("It is not Integer");
        }
        return (Integer) fields.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Long)) {
            throw new ColumnFormatException("It is not Long");
        }
        return (Long) fields.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Byte)) {
            throw new ColumnFormatException("It is not Byte");
        }
        return (Byte) fields.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Float)) {
            throw new ColumnFormatException("It is not Float");
        }
        return (Float) fields.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Double)) {
            throw new ColumnFormatException("It is not Double");
        }
        return (Double) fields.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof Boolean)) {
            throw new ColumnFormatException("It is not Boolean");
        }
        return (Boolean) fields.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        goodColumnIndex(columnIndex);
        if (!(fields.get(columnIndex) instanceof String) && fields.get(columnIndex) != null) {
            throw new ColumnFormatException("It is not String");
        }
        return (String) fields.get(columnIndex);
    }
}
