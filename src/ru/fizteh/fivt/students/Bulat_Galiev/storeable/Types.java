package ru.fizteh.fivt.students.Bulat_Galiev.storeable;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class Types {

    private HashMap<Class<?>, SerializeObject> classSerializerMap = new HashMap<>();
    private HashMap<Class<?>, DeserializeString> classDeserializerMap = new HashMap<>();

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

    public Types() {
        classSerializerMap.put(Long.class, Object::toString);
        classSerializerMap.put(Integer.class, Object::toString);
        classSerializerMap.put(Byte.class, Object::toString);
        classSerializerMap.put(Double.class, Object::toString);
        classSerializerMap.put(Float.class, Object::toString);
        classSerializerMap.put(Boolean.class, Object::toString);
        classSerializerMap.put(String.class, object -> "\"" + object + "\"");

        classDeserializerMap.put(Integer.class, Integer::valueOf);
        classDeserializerMap.put(Long.class, Long::valueOf);
        classDeserializerMap.put(Byte.class, Byte::valueOf);
        classDeserializerMap.put(Double.class, Double::valueOf);
        classDeserializerMap.put(Float.class, Float::valueOf);
        classDeserializerMap.put(Boolean.class, string -> {
            if (string.trim().toLowerCase().equals("true")) {
                return true;
            }
            if (string.trim().toLowerCase().equals("false")) {
                return false;
            }
            throw new ParseException("Wrong Boolean type", 0);
        });
        classDeserializerMap.put(String.class,
                string -> {
                    if (string.length() > 1 && string.charAt(0) == '"'
                            && string.charAt(string.length() - 1) == '"') {
                        return string.substring(1, string.length() - 1);
                    }
                    throw new ParseException("Wrong String type", 0);
                });

    }

    public String serialize(Table table, Storeable value) {
        int numberOfElements = table.getColumnsCount();
        StringBuilder sb = new StringBuilder("[");
        String prefix = "";
        for (int i = 0; i < numberOfElements; ++i) {
            sb.append(prefix);
            prefix = ", ";
            if (value.getColumnAt(i) == null) {
                sb.append("null");
            } else {
                sb.append(classSerializerMap.get(table.getColumnType(i))
                        .getString(value.getColumnAt(i)));
            }
        }
            sb.append("]");
        return sb.toString();
    }

    public Storeabledb deserialize(Table table, String value)
            throws ParseException {
        if (value == null) {
            throw new ParseException("null value", 0);
        }
        value = value.trim();
        if (value.length() < 3 || value.charAt(0) != '['
                || value.charAt(value.length() - 1) != ']') {
            throw new ParseException("invalid JSON format", 0);
        }
        List<Object> values = new ArrayList<>();
        String[] tokens = value.substring(1, value.length() - 1).split(",");
        for (String string : tokens) {
            if (string.isEmpty()) {
                throw new ParseException("empty object in JSONArray", 0);
            }
        }
        if (tokens.length != table.getColumnsCount()) {
            throw new ParseException("invalid number of columns", 0);
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                if (tokens[i].trim().toLowerCase().equals("null")) {
                    values.add(null);
                } else {
                    values.add(classDeserializerMap.get(table.getColumnType(i))
                            .getObject(tokens[i].trim()));
                }
            } catch (NumberFormatException e) {
                throw new ParseException("invalid type of value", 0);
            }
        }

        return new Storeabledb(values);
    }

    interface DeserializeString {
        Object getObject(String string) throws ParseException;
    }

    interface SerializeObject {
        String getString(Object object);
    }
}
