package ru.fizteh.fivt.students.VasilevKirill.proxy.structures;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Kirill on 15.11.2014.
 */
public class MyStorable implements Storeable {
    List<Object> dataList;
    List<Class<?>> typeList;

    public MyStorable(Class[] typeList) {
        this.typeList = Arrays.asList(typeList);
        dataList = new ArrayList<>(typeList.length);
        for (int i = 0; i < typeList.length; ++i) {
            dataList.add(null);
        }
    }

    public MyStorable(List<Class<?>> listOfTypes) {
        this.typeList = listOfTypes;
        dataList = new ArrayList<>(typeList.size());
        for (int i = 0; i < typeList.size(); ++i) {
            dataList.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (value == null) {
            dataList.set(columnIndex, null);
            return;
        }
        if (value.getClass() != typeList.get(columnIndex)) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        dataList.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        return dataList.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Integer.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Integer)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Integer) result;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Long.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Long)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Long) result;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Byte.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Byte)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Byte) result;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Float.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Float)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Float) result;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Double.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Double)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Double) result;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != Boolean.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof Boolean)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return (Boolean) result;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size() || columnIndex >= dataList.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: incorrect index");
        }
        if (typeList.get(columnIndex) != String.class) {
            throw new ColumnFormatException("MyStoreable: incorrect column format");
        }
        if (dataList.get(columnIndex) == null) {
            return null;
        }
        Object result = dataList.get(columnIndex);
        if (!(result instanceof String)) {
            throw new ColumnFormatException("MyStoreable: error in data");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        try {
            JSONArray arr = new JSONArray(dataList);
            String result = arr.toString();
            result = result.replace("null", "");
            return result;
        } catch (JSONException e) {
            return null;
        }
    }

    public String toFileFormat() {
        try {
            JSONArray arr = new JSONArray(dataList);
            return arr.toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
