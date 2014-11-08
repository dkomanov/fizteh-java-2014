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
}
