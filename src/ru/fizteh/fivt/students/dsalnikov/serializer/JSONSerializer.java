package ru.fizteh.fivt.students.dsalnikov.serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.text.ParseException;

public class JSONSerializer {
    private static Object getRightClassObject(Table table, JSONArray input, int index)
            throws ColumnFormatException, JSONException {
        if (input.get(index) == JSONObject.NULL || input.get(index) == null) {
            return null;
        }
        if (input.get(index).getClass() == Integer.class && table.getColumnType(index) == Integer.class) {
            return input.getInt(index);
        } else if (input.get(index).getClass() == Long.class && table.getColumnType(index) == Long.class) {
            return input.getLong(index);
        } else if (input.get(index).getClass() == Integer.class && table.getColumnType(index) == Long.class) {
            return input.getLong(index);
        } else if (input.get(index).getClass() == Integer.class && table.getColumnType(index) == Byte.class) {
            Integer tempInt = input.getInt(index);
            return tempInt.byteValue();
        } else if (input.get(index).getClass() == Double.class && table.getColumnType(index) == Float.class) {
            Double tempDbl = input.getDouble(index);
            return tempDbl.floatValue();
        } else if (input.get(index).getClass() == Double.class && table.getColumnType(index) == Double.class) {
            return input.getDouble(index);
        } else if (input.get(index).getClass() == Boolean.class && table.getColumnType(index) == Boolean.class) {
            return input.getBoolean(index);
        } else if (input.get(index).getClass() == String.class && table.getColumnType(index) == String.class) {
            return input.getString(index);
        } else {
            throw new ColumnFormatException("type mismatch");
        }
    }


    public static Storeable deserialize(Table table, String value, TableProvider tableProvider) throws ParseException {
        Storeable result = tableProvider.createFor(table);
        try {
            JSONArray input = new JSONArray(value);
            for (Integer i = 0; i < input.length(); ++i) {
                try {
                    result.setColumnAt(i, JSONSerializer.getRightClassObject(table, input, i));
                } catch (ColumnFormatException | IndexOutOfBoundsException exc) {
                    throw new ParseException("JSONserializer: deserialize: can not set column at,"
                            + " type mismatch or out of bounds", 0);
                }
            }
        } catch (JSONException exc) {
            throw new ParseException("JSONserializer: deserialize: string not valid to make JSON object", 0);
        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        JSONArray output = new JSONArray();
        Integer columnsCountOfGivenTable = table.getColumnsCount();
        for (Integer i = 0; i < columnsCountOfGivenTable; ++i) {
            output.put(value.getColumnAt(i));
        }
        return output.toString();
    }
}
