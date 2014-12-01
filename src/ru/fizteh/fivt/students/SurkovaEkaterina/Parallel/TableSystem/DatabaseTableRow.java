package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.StoreableUsage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableRow implements Storeable {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    List<Object> columns = new ArrayList<Object>();

    public DatabaseTableRow(List<Class<?>> types) {
        this.classes.addAll(types);

        for (int index = 0; index < classes.size(); ++index) {
            columns.add(null);
        }
    }

    public DatabaseTableRow() {}

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkColumnClass(columnIndex, value);
            try {
                StoreableUsage.checkValue(value, value.getClass());
            } catch (ParseException e) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Incorrect value!");
            }
        }
        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Integer.class);
        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Long.class);
        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Byte.class);
        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Double.class);
        return (Double) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Float.class);
        return (Float) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, Boolean.class);
        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkColumnClass(columnIndex, String.class);
        return (String) columns.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Object value : columns) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            if (value == null) {
                sb.append("null");
            } else {
                sb.append(value.toString());
            }
        }
        return String.format("%s[%s]", getClass().getSimpleName(), sb.toString());
    }

    public void addColumn(Class<?> columnType) {
        classes.add(columnType);
        columns.add(null);
    }

    public void setColumns(List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != classes.size()) {
            throw new IndexOutOfBoundsException();
        }

        columns.clear();

        for (int index = 0; index < values.size(); ++index) {
            if (values.get(index).toString().trim().equals("")) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Column value can not be empty!");
            }
            checkColumnClass(index, values.get(index));
            columns.add(values.get(index));
        }
    }

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException(String.format("Index out of range: %d", index));
        }
    }

    private void checkColumnClass(int columnIndex, Object value) throws ColumnFormatException {
        if (!value.getClass().isAssignableFrom(classes.get(columnIndex))) {
            throw new ColumnFormatException(String.format(
                    "Incorrect type: expected type: %s, actual type: %s",
                    classes.get(columnIndex).getName(), value.getClass().getName()));
        }
    }
}
