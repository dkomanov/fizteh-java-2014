package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TypeManager {
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
        TYPES.put("string", String.class);

        NAMES = new HashMap<>();
        NAMES.put(Integer.class, "int");
        NAMES.put(Long.class, "long");
        NAMES.put(Byte.class, "byte");
        NAMES.put(Float.class, "float");
        NAMES.put(Double.class, "double");
        NAMES.put(Boolean.class, "boolean");
        NAMES.put(String.class, "string");
    }

    public static Class<?> getClass(String typeName) throws IOException {
        if (!TYPES.containsKey(typeName)) {
            throw new IOException("Unknown type name " + typeName);
        }
        return TYPES.get(typeName);
    }

    public static String getName(Class<?> type) throws IOException {
        if (!NAMES.containsKey(type)) {
            throw new IOException("Class " + type.getName() + " cannot be used");
        }
        return NAMES.get(type);
    }

    public static List<Class<?>> getClasses(String list) {
        List<Class<?>> listClasses = new ArrayList<>();
        for (String typeName : list.split("\\s+", 0)) {
            listClasses.add(TYPES.get(typeName));
        }
        return listClasses;
    }

    public static String getNames(List<Class<?>> types) throws IOException {
        StringBuilder names = new StringBuilder();
        for (Class type : types) {
            if (names.length() > 0) {
                names.append(' ');
            }
            names.append(getName(type));
        }
        return names.toString();
    }
}
