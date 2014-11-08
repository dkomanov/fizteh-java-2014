package ru.fizteh.fivt.students.standy66_new.utility;

/**
 * Created by andrew on 08.11.14.
 */
public class ClassUtility {
    public static Class forName(String className) {
        switch (className) {
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
                throw new IllegalArgumentException("className is not supported");
        }
    }

    public static String toString(Class<?> cls) {
        if (cls == Integer.class) {
            return "int";
        } else if (cls == Long.class) {
            return "long";
        } else if (cls == Byte.class) {
            return "byte";
        } else if (cls == Float.class) {
            return "float";
        } else if (cls == Double.class) {
            return "double";
        } else if (cls == Boolean.class) {
            return "boolean";
        } else if (cls == String.class) {
            return "String";
        } else {
            throw new IllegalArgumentException("class is not supported");
        }
    }
}
