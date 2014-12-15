package ru.fizteh.fivt.students.egor_belikov.Parallel;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySerializer {

    private static Map<Class<?>, String> forwardMap = new HashMap<>();
    private static Map<String, Class<?>> backwardMap = new HashMap<>();

    static {
        forwardMap.put(Integer.class, "int");
        forwardMap.put(Long.class, "long");
        forwardMap.put(Float.class, "float");
        forwardMap.put(Double.class, "double");
        forwardMap.put(Byte.class, "byte");
        forwardMap.put(Boolean.class, "boolean");
        forwardMap.put(String.class, "String");

        backwardMap.put("int", Integer.class);
        backwardMap.put("long", Long.class);
        backwardMap.put("float", Float.class);
        backwardMap.put("double", Double.class);
        backwardMap.put("byte", Byte.class);
        backwardMap.put("boolean", Boolean.class);
        backwardMap.put("String", String.class);
    }

    public static String returningString(Class<?> type) {
        return forwardMap.get(type);
    }

    public static Class<?> returningClass(String string) {
        return backwardMap.get(string);
    }

    interface Reader {
        Object getObject(String string) throws Exception;
    }

    interface Writer {
        String getString(Object object);
    }

    private static Map<Class, Reader> readerMap = new HashMap<>();
    private static Map<Class, Writer> writerMap = new HashMap<>();

    public MySerializer() {
        readerMap.put(Integer.class, Integer::valueOf);
        readerMap.put(Long.class, Long::valueOf);
        readerMap.put(Float.class, Float::valueOf);
        readerMap.put(Double.class, Double::valueOf);
        readerMap.put(Byte.class, Byte::valueOf);
        readerMap.put(Boolean.class, string -> {
            if (string.trim().toLowerCase().equals("true")) {
                return true;
            } else if (string.trim().toLowerCase().equals("false")) {
                return false;
            }
            throw new Exception("not a valid boolean value");
        });
        readerMap.put(String.class, string -> {
            if (string.length() > 1 && string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
                return string.substring(1, string.length() - 1);
            }
            throw new Exception("not a valid String value");
        });

        writerMap.put(Integer.class, Object::toString);
        writerMap.put(Long.class, Object::toString);
        writerMap.put(Float.class, Object::toString);
        writerMap.put(Double.class, Object::toString);
        writerMap.put(Byte.class, Object::toString);
        writerMap.put(Boolean.class, Object::toString);
        writerMap.put(String.class, object -> "\"" + object + "\"");
    }

    public static Storeable deserialize(Table table, String value, List<Class<?>> signature) throws ParseException {
        try {
            value = value.trim();
            if (value == null || (value.charAt(0) != '[' && value.charAt(value.length() - 1) != ']')) {
                throw new IllegalArgumentException("StoreableParser: illegal argument");
            }
            List<Object> values = new ArrayList<>();
            JSONArray parser = new JSONArray(value);
            if (parser.length() != table.getColumnsCount()) {
                throw new ParseException("column count mismatch", 0);
            }
            for (int i = 0; i < parser.length(); ++i) {
                try {
                    if (parser.isNull(i)) {
                        values.add(null);
                    } else {
                        values.add(readerMap.get(table.getColumnType(i)).getObject(parser.get(i).toString().trim()));
                    }
                } catch (ClassCastException e) {
                    throw new ParseException(parser.get(i).toString(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new MyStoreable(values, signature);
        } catch (JSONException e) {
            return null;
        }
    }


    public String serialize(Table table, Storeable value) {
        int c = table.getColumnsCount();
        if (c != ((MyStoreable) value).columnsNum) {
            throw new ColumnFormatException("column count mismatch");
        }
        StringBuilder tempStringBuilder = new StringBuilder("[");
        for (int i = 0; i < c; i++) {
            if (value.getColumnAt(i) == null) {
                tempStringBuilder.append("null");
            } else {
                writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i));
                tempStringBuilder.append(writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            }
            tempStringBuilder.append(',');
        }
        tempStringBuilder.setCharAt(tempStringBuilder.length() - 1, ']');
        return tempStringBuilder.toString();
    }
}
