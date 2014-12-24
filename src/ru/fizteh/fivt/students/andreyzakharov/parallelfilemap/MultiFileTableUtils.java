package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap;

import java.util.HashMap;
import java.util.Map;

public class MultiFileTableUtils {
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

    private MultiFileTableUtils() {
        // not used
    }

    public static String classToString(Class<?> type) {
        return ctsMap.get(type);
    }

    public static Class<?> stringToClass(String string) {
        return stcMap.get(string);
    }
}
