package ru.fizteh.fivt.students.VasilevKirill.Storeable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kirill on 15.11.2014.
 */
public class MyStorable implements Storeable {
    Object[] dataList;
    Class[] typeList;

    public MyStorable(Class[] typeList) {
        this.typeList = typeList;
        dataList = new Object[typeList.length];
    }

    public MyStorable(List<Class<?>> listOfTypes) {
        int num = listOfTypes.size();
        typeList = new Class[num];
        for (int i = 0; i < num; ++i) {
            typeList[i] = listOfTypes.get(i);
        }
        dataList = new Object[num];
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (value == null) {
            dataList[columnIndex] = null;
            return;
        }
        if (value.getClass() != typeList[columnIndex]) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        dataList[columnIndex] =  value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        return dataList[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Integer.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Integer)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Integer) result;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Long.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Long)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Long) result;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Byte.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Byte)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Byte) result;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Float.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Float)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Float) result;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Double.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Double)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Double) result;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != Boolean.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof Boolean)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Boolean) result;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length || columnIndex >= dataList.length) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList[columnIndex] != String.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList[columnIndex] == null) {
            return null;
        }
        Object result = dataList[columnIndex];
        if (!(result instanceof String)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return result.toString();
    }

    public List<Object> dataToList() {
        List<Object> result = new ArrayList<>(dataList.length);
        for (Object it : dataList) {
            result.add(it);
        }
        return result;
    }

    @Override
    public String toString() {
        JSONArray arr;
        try {
            arr = new JSONArray(dataList);
        } catch (JSONException e) {
            return null;
        }
        return arr.toString();
    }
}
