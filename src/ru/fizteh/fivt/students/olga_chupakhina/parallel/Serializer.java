package ru.fizteh.fivt.students.olga_chupakhina.parallel;

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

public class Serializer {

    private static Map<Class<?>, String> ctsMap = new HashMap<>();
    private static Map<String, Class<?>> stcMap = new HashMap<>();

    static {
        ctsMap.put(Integer.class, "int");
        ctsMap.put(Long.class, "long");
        ctsMap.put(Float.class, "float");
        ctsMap.put(Double.class, "double");
        ctsMap.put(Byte.class, "byte");
        ctsMap.put(Boolean.class, "boolean");
        ctsMap.put(String.class, "String");

        stcMap.put("int", Integer.class);
        stcMap.put("long", Long.class);
        stcMap.put("float", Float.class);
        stcMap.put("double", Double.class);
        stcMap.put("byte", Byte.class);
        stcMap.put("boolean", Boolean.class);
        stcMap.put("String", String.class);
    }

    public static String classToString(Class<?> type) {
        return ctsMap.get(type);
    }

    public static Class<?> stringToClass(String string) {
        return stcMap.get(string);
    }

    interface Reader {
        Object getObject(String string) throws Exception;
    }

    interface Writer {
        String getString(Object object);
    }

    private static Map<Class, Reader> readerMap = new HashMap<>();
    private static Map<Class, Writer> writerMap = new HashMap<>();

    public Serializer() {
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
            if ((value.charAt(0) != '[' && value.charAt(value.length() - 1) != ']') || value == null) {
                throw new IllegalArgumentException("StoreableParser: illegal argument");
            }
            List<Object> values = new ArrayList<>();
            JSONArray json = new JSONArray(value);
            if (json.length() != table.getColumnsCount()) {
                throw new ParseException("column count mismatch", 0);
            }
            for (int i = 0; i < json.length(); ++i) {
                try {
                    if (json.isNull(i)) {
                        values.add(null);
                    } else {
                        values.add(readerMap.get(table.getColumnType(i)).getObject(json.get(i).toString().trim()));
                    }
                } catch (ClassCastException e) {
                    throw new ParseException(json.get(i).toString(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new OStoreable(values, signature);
        } catch (JSONException e) {
            return null;
        }
    }

    public String serialize(Table table, Storeable value) {
        int c = table.getColumnsCount();
        if (c != ((OStoreable) value).getColumnsNum()) {
            throw new ColumnFormatException("column count mismatch");
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < c; i++) {
            if (value.getColumnAt(i) == null) {
                sb.append("null");
            } else {
                writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i));
                sb.append(writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            }
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }
}
