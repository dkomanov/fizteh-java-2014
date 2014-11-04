package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

public class MultiFileTableUtils {
    private MultiFileTableUtils() {
        // not used
    }

    public static String classToString(Class<?> type) {
        if (type == Integer.class) return "int";
        if (type == Long.class) return "long";
        if (type == Float.class) return "float";
        if (type == Double.class) return "double";
        if (type == Boolean.class) return "bool";
        if (type == Byte.class) return "byte";
        if (type == String.class) return "String";
        return null;
    }

    public static Class<?> stringToClass(String string) throws ClassNotFoundException {
        switch (string) {
            case "int": return Integer.class;
            case "long": return Long.class;
            case "float": return Float.class;
            case "double": return Double.class;
            case "bool": return Boolean.class;
            case "byte": return Byte.class;
            case "String": return String.class;
            default: throw new ClassNotFoundException("no such class");
        }
    }
}
