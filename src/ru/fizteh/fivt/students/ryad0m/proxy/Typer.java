package ru.fizteh.fivt.students.ryad0m.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Typer {

    private static final HashMap<String, Class<?>> TYPES;
    private static final HashMap<Class<?>, String> STRINGS;

    static {
        TYPES = new HashMap<>();
        TYPES.put("int", Integer.class);
        TYPES.put("long", Long.class);
        TYPES.put("byte", Byte.class);
        TYPES.put("float", Float.class);
        TYPES.put("double", Double.class);
        TYPES.put("boolean", Boolean.class);
        TYPES.put("String", String.class);

        STRINGS = new HashMap<>();
        STRINGS.put(Integer.class, "int");
        STRINGS.put(Long.class, "long");
        STRINGS.put(Byte.class, "byte");
        STRINGS.put(Float.class, "float");
        STRINGS.put(Double.class, "double");
        STRINGS.put(Boolean.class, "boolean");
        STRINGS.put(String.class, "String");
    }

    public static Class<?> typeByName(String name) throws IOException {
        if (!TYPES.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return TYPES.get(name);
    }

    public static List<Class<?>> typeListFromString(String list) throws IOException {
        List<Class<?>> result = new Vector<>();
        for (String name : list.split("\\s+", 0)) {
            result.add(typeByName(name));
        }
        return result;
    }

    public static String nameByType(Class type) throws IllegalArgumentException {
        if (!STRINGS.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return STRINGS.get(type);
    }

    public static String stringFromTypeList(List<Class<?>> types) {
        StringBuilder s = new StringBuilder();
        for (Class type : types) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(nameByType(type));
        }
        return s.toString();
    }
}
