package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user1 on 09.11.2014.
 */
public class TableRowSerializer {

    private HashMap<Class, RowSerializer> writeMap = new HashMap<>();
    private HashMap<Class, RowDeserializer> readMap = new HashMap<>();

    private static Map<Class<?>, String> classStringMap = new HashMap<>();
    private static Map<String, Class<?>> stringClassMap = new HashMap<>();

    static {
        classStringMap.put(Integer.class, "int");
        classStringMap.put(Long.class, "long");
        classStringMap.put(Float.class, "float");
        classStringMap.put(Double.class, "double");
        classStringMap.put(Byte.class, "byte");
        classStringMap.put(Boolean.class, "boolean");
        classStringMap.put(String.class, "String");

        stringClassMap.put("int", Integer.class);
        stringClassMap.put("long", Long.class);
        stringClassMap.put("float", Float.class);
        stringClassMap.put("double", Double.class);
        stringClassMap.put("byte", Byte.class);
        stringClassMap.put("boolean", Boolean.class);
        stringClassMap.put("String", String.class);

    }

    public static String classToString(Class<?> type) {
        return classStringMap.get(type);
    }

    public static Class<?> stringToClass(String string) {
        return stringClassMap.get(string);
    }

    public TableRowSerializer() {
        writeMap.put(Long.class, Object::toString);
        writeMap.put(Integer.class, Object::toString);
        writeMap.put(Byte.class, Object::toString);
        writeMap.put(Double.class, Object::toString);
        writeMap.put(Float.class, Object::toString);
        writeMap.put(Boolean.class, Object::toString);
        writeMap.put(String.class, object -> "\"" + object + "\"");

        readMap.put(Integer.class, Integer::valueOf);
        readMap.put(Long.class, Long::valueOf);
        readMap.put(Byte.class, Byte::valueOf);
        readMap.put(Double.class, Double::valueOf);
        readMap.put(Float.class, Float::valueOf);
        readMap.put(Boolean.class, string -> {
            if (string.trim().toLowerCase().equals("true")) {
                return true;
            }
            if (string.trim().toLowerCase().equals("false")) {
                return false;
            }
            throw new ParseException("Wrong Boolean type", 0);
        });
        readMap.put(String.class, string -> {
            if (string.length() > 1 && string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
                return string.substring(1, string.length() - 1);
            }
            throw new ParseException("Wrong String type", 0);
        });

    }

    public String serialize(Table table, Storeable value) {
        int numberOfElements = table.getColumnsCount();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < numberOfElements; ++i) {
            if (value.getColumnAt(i) == null) {
                sb.append("null");
            } else {
                sb.append(writeMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            }
            sb.append(", ");
        }
        if (numberOfElements > 0) {
            sb.deleteCharAt(sb.length() - 2);
            sb.setCharAt(sb.length() - 1, ']');
        }
        return sb.toString();
    }

    public TableRow deserialize(Table table, String value) {

        return null;
    }


    interface RowDeserializer {
        Object getObject(String string) throws ParseException;
    }

    interface RowSerializer {
        String getString(Object object);
    }
}

