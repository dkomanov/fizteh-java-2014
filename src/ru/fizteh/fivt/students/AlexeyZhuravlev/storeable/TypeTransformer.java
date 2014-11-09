package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author AlexeyZhuravlev
 */
public class TypeTransformer {

    private static final HashMap<String, Class<?>> TYPES;

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

    public static Class<?> typeByName(String name) throws Exception {
        if (!TYPES.containsKey(name)) {
            throw new Exception("Unknown type name");
        }
        return TYPES.get(name);
    }

    public static List<Class<?>> typeListFromString(String list) throws Exception {
        List<Class<?>> result = new Vector<>();
        for (String name: list.split("\\s+")) {
            result.add(typeByName(name));
        }
        return result;
    }
}
