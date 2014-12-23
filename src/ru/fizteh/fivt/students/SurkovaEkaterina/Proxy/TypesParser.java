package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TypesParser {
    INTEGER("int", Integer.class) {
        public Object parseValue(String str) {
            return Integer.parseInt(str);
        }
    },
    LONG("long", Long.class) {
        public Object parseValue(String str) {
            return Long.parseLong(str);
        }
    },
    BYTE("byte", Byte.class) {
        public Object parseValue(String str) {
            return Byte.parseByte(str);
        }
    },
    FLOAT("float", Float.class) {
        public Object parseValue(String str) {
            return Float.parseFloat(str);
        }
    },
    DOUBLE("double", Double.class) {
        public Object parseValue(String str) {
            return Double.parseDouble(str);
        }
    },
    BOOLEAN("boolean", Boolean.class) {
        public Object parseValue(String str) {
            return Boolean.parseBoolean(str);
        }
    },
    STRING("string", String.class) {
        public Object parseValue(String str) {
            return str;
        }
    };

    private String name;
    private Class type;

    private TypesParser(String newName, Class newType) {
        name = newName;
        type = newType;
    }

    private static final Map<String, TypesParser> NAMES_MATCHING;
    private static final Map<Class, TypesParser> CLASSES_MATCHING;

    static {
        HashMap<String, TypesParser> tempByName = new HashMap<String, TypesParser>();
        HashMap<Class, TypesParser> tempByClass = new HashMap<Class, TypesParser>();

        for (TypesParser value : values()) {
            tempByName.put(value.name, value);
            tempByClass.put(value.type, value);
        }

        NAMES_MATCHING = Collections.unmodifiableMap(tempByName);
        CLASSES_MATCHING = Collections.unmodifiableMap(tempByClass);
    }

    public static String getNameByType(Class type) {
        TypesParser typesParser = CLASSES_MATCHING.get(type);

        if (typesParser == null) {
            throw new IllegalArgumentException("Unknown type!");
        }

        return typesParser.name;
    }

    public static Class getTypeByName(String name) {
        TypesParser typesParser = NAMES_MATCHING.get(name);

        if (typesParser == null) {
            throw new IllegalArgumentException("Unknown type!");
        }

        return typesParser.type;
    }

    public abstract Object parseValue(String string);

    public static Object parseByClass(String str, Class type) {
        TypesParser typesParser = CLASSES_MATCHING.get(type);

        if (typesParser == null) {
            throw new IllegalArgumentException("Unknown type!");
        }

        return typesParser.parseValue(str);
    }
}
