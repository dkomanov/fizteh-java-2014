package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class TypeTransformer {

    private static final HashMap<String, Class<?>> TYPES;
    private static final HashMap<Class<?>, String> CLASSES;

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
        CLASSES = new HashMap<>();
        CLASSES.put(Integer.class, "int");
        CLASSES.put(Long.class, "long");
        CLASSES.put(Byte.class, "byte");
        CLASSES.put(Float.class, "float");
        CLASSES.put(Double.class, "double");
        CLASSES.put(Boolean.class, "boolean");
        CLASSES.put(String.class, "String");
    }

    private static Class<?> classByName(String name) throws IOException {
        if (!TYPES.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return TYPES.get(name);
    }

    public static List<Class<?>> classListFromString(String list) throws Exception {
        List<Class<?>> result = new Vector<>();
        for (String name: list.split("\\s+", 0)) {
            result.add(classByName(name));
        }
        return result;
    }

    private static String nameByClass(Class type) throws IllegalArgumentException {
        if (!CLASSES.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return CLASSES.get(type);
    }

    public static String stringFromClassList(List<Class<?>> types) throws IllegalArgumentException {
        StringBuilder s = new StringBuilder();
        for (Class type: types) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(nameByClass(type));
        }
        return s.toString();
    }
}
