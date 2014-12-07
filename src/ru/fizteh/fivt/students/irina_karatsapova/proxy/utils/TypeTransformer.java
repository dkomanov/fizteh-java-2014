package ru.fizteh.fivt.students.irina_karatsapova.proxy.utils;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ColumnFormatException;

import java.util.HashMap;

public class TypeTransformer {
    private static final HashMap<String, Class<?>> TYPES;
    private static final HashMap<Class<?>, String> NAMES;

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
        NAMES = new HashMap<>();
        NAMES.put(Integer.class, "int");
        NAMES.put(Long.class, "long");
        NAMES.put(Byte.class, "byte");
        NAMES.put(Float.class, "float");
        NAMES.put(Double.class, "double");
        NAMES.put(Boolean.class, "boolean");
        NAMES.put(String.class, "String");
    }

    public static String getStringByType(Class type) {
        if (!NAMES.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return NAMES.get(type);
    }

    public static Class<?> getTypeByName(String name) {
        if (!TYPES.containsKey(name)) {
            throw new ColumnFormatException("Unknown type name: " + name);
        }
        return TYPES.get(name);
    }
}
