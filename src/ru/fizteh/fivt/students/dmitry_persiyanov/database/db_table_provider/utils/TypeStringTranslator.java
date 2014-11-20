package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils;

/**
 * Created by drack3800 on 20.11.2014.
 */
public class TypeStringTranslator {
    public static Class<?> getTypeByStringName(final String stringName) {
        switch (stringName.trim()) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                return null;
        }
    }

    public static String getStringNameByType(final Class<?> type) {
        if (type.equals(Integer.class)) {
            return "int";
        } else if (type.equals(Long.class)) {
            return "long";
        } else if (type.equals(Byte.class)) {
            return "byte";
        } else if (type.equals(Float.class)) {
            return "float";
        } else if (type.equals(Double.class)) {
            return "double";
        } else if (type.equals(Boolean.class)) {
            return "boolean";
        } else if (type.equals(String.class)) {
            return "String";
        } else {
            throw new IllegalArgumentException("unsupported type: " + type.getName());
        }
    }
}
