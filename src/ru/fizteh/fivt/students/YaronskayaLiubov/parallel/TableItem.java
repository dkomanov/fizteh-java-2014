package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class TableItem implements Storeable {
    private Object[] items;
    private int itemsCount;
    Table table;

    public TableItem(Table table) {
        this.table = table;
        itemsCount = table.getColumnsCount();
        items = new Object[itemsCount];
        for (int i = 0; i < itemsCount; ++i) {
            items[i] = null;
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (table.getColumnType(columnIndex) != value.getClass()) {
            throw new ColumnFormatException("illegal column format: Expected: "
                    + items[columnIndex].getClass().toString() + " Actual: "
                    + value.getClass().toString());
        }
        items[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        return items[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Column format is not Integer");
        }
        return (Integer) items[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("Column format is not Long");
        }
        return (Long) items[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("Column format is not Byte");
        }
        return (Byte) items[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("Column format is not Float");
        }
        return (Float) items[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("Column format is not Double");
        }
        return (Double) items[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("Column format is not Boolean");
        }
        return (Boolean) items[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= itemsCount) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        if (items[columnIndex] == null) {
            return null;
        }
        if (!(items[columnIndex] instanceof String)) {
            throw new ColumnFormatException("Column format is not String");
        }
        return (String) items[columnIndex];
    }

    public int getItemsCount() {
        return itemsCount;
    }
}
