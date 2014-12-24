package ru.fizteh.fivt.students.kinanAlsarmini.storable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TypesFormatter {
    INTEGER("int", Integer.class) {
        public Object parseValue(String string) {
            return Integer.parseInt(string);
        }
    },
    LONG("long", Long.class) {
        public Object parseValue(String string) {
            return Long.parseLong(string);
        }
    },
    BYTE("byte", Byte.class) {
        public Object parseValue(String string) {
            return Byte.parseByte(string);
        }
    },
    FLOAT("float", Float.class) {
        public Object parseValue(String string) {
            return Float.parseFloat(string);
        }
    },
    DOUBLE("double", Double.class) {
        public Object parseValue(String string) {
            return Double.parseDouble(string);
        }
    },
    BOOLEAN("boolean", Boolean.class) {
        public Object parseValue(String string) {
            return Boolean.parseBoolean(string);
        }
    },
    STRING("String", String.class) {
        public Object parseValue(String string) {
            return string;
        }
    };

    private final String name;
    private final Class<?> type;

    private TypesFormatter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    private static final Map<String, TypesFormatter> TYPES_BY_NAME;
    private static final Map<Class<?>, TypesFormatter> TYPES_BY_CLASS;

    static {
        HashMap<String, TypesFormatter> tempByName = new HashMap<>();
        HashMap<Class<?>, TypesFormatter> tempByClass = new HashMap<>();

        for (TypesFormatter value : values()) {
            tempByName.put(value.name, value);
            tempByClass.put(value.type, value);
        }

        TYPES_BY_NAME = Collections.unmodifiableMap(tempByName);
        TYPES_BY_CLASS = Collections.unmodifiableMap(tempByClass);
    }

    public static Class<?> getTypeByName(String name) {
        TypesFormatter typesFormatter = TYPES_BY_NAME.get(name);

        if (typesFormatter == null) {
            throw new IllegalArgumentException("unknown type");
        }

        return typesFormatter.type;
    }

    public static String getSimpleName(Class<?> type) {
        TypesFormatter typesFormatter = TYPES_BY_CLASS.get(type);

        if (typesFormatter == null) {
            throw new IllegalArgumentException("unknown type");
        }

        return typesFormatter.name;
    }

    public abstract Object parseValue(String string);

    public static Object parseByClass(String string, Class<?> type) {
        TypesFormatter typesFormatter = TYPES_BY_CLASS.get(type);

        if (typesFormatter == null) {
            throw new IllegalArgumentException("unknown type");
        }

        return typesFormatter.parseValue(string);
    }
}
