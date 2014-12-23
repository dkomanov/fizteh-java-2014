package storeable.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypesTransformer {
    private static Map<String, Class<?>> types;
    private static Map<Class<?>, String> strings;

    static {
        types = new HashMap<String, Class<?>>();
        types.put("int", Integer.class);
        types.put("long", Long.class);
        types.put("byte", Byte.class);
        types.put("float", Float.class);
        types.put("double", Double.class);
        types.put("boolean", Boolean.class);
        types.put("String", String.class);

        strings = new HashMap<Class<?>, String>();
        strings.put(Integer.class, "int");
        strings.put(Long.class, "long");
        strings.put(Byte.class, "byte");
        strings.put(Float.class, "float");
        strings.put(Double.class, "double");
        strings.put(Boolean.class, "boolean");
        strings.put(String.class, "String");
    }

    public static Class<?> toType(String string) {
        if (types.get(string) == null) {
            throw new IllegalArgumentException("Bad type");
        }
        return types.get(string);
    }

    public static String toString(Class<?> type) {
        if (strings.get(type) == null) {
            throw new IllegalArgumentException("Bad type");
        }
        return strings.get(type);
    }

    public static List<Class<?>> toListTypes(String[] strings) {
        List<Class<?>> types = new ArrayList<Class<?>>();

        for (int i = 0; i < strings.length; i++) {
            types.add(toType(strings[i]));
        }

        return types;
    }
}
