package ru.fizteh.fivt.students.gudkov394.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.gudkov394.Storable.src.TableContents;

/**
 * Created by kagudkov on 30.11.14.
 */
public class StoreableWithToString implements Storeable {

    Storeable origin;

    public StoreableWithToString(Storeable tmp) {
        origin = tmp;
    }

    public Storeable getStoreable() {
        return origin;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        origin.setColumnAt(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return origin.getColumnAt(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getIntAt(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getLongAt(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getByteAt(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getFloatAt(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getDoubleAt(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getBooleanAt(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return origin.getStringAt(columnIndex);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Object value : ((TableContents) origin).getFields()) {
            if (value == null) {
                builder.append("");
            } else {
                builder.append(value.toString());
            }
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return this.getClass().getSimpleName() + "[" + builder.toString();
    }


}
