package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Lenovo on 23.11.2014.
 */
public class TypeTransformer {

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
    }

    static {
        STRINGS = new HashMap<>();
        STRINGS.put(Integer.class, "int");
        STRINGS.put(Long.class, "long");
        STRINGS.put(Byte.class, "byte");
        STRINGS.put(Float.class, "float");
        STRINGS.put(Double.class, "double");
        STRINGS.put(Boolean.class, "boolean");
        STRINGS.put(String.class, "String");
    }

    public static Class<?> typeFromName(String name) throws IOException {
        if (!TYPES.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return TYPES.get(name);
    }

    public static List<Class<?>> typeListFromString(String list) throws Exception {
        List<Class<?>> result = new Vector<>();
        for (String name: list.split("\\s+", 0)) {
            result.add(typeFromName(name));
        }
        return result;
    }

    public static String nameFromType(Class type) throws IllegalArgumentException {
        if (!STRINGS.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return STRINGS.get(type);
    }

    public static String stringFromTypeList(List<Class<?>> types) throws IllegalArgumentException {
        StringBuilder s = new StringBuilder();
        for (Class type: types) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(nameFromType(type));
        }
        return s.toString();
    }

    public static List<String> stringListFromTypeList(List<Class<?>> types) throws IllegalArgumentException {
        List<String> list = new LinkedList<String>();
        for (Class type: types) {
            list.add(nameFromType(type));
        }
        return list;
    }
}
