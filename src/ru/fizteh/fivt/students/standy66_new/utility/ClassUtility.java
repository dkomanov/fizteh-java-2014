package ru.fizteh.fivt.students.standy66_new.utility;

/**
 * Created by andrew on 08.11.14.
 */
public class ClassUtility {
    public static Class forName(String className) {
        switch (className) {
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "byte":
                return byte.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            case "String":
                return String.class;
            default:
                throw new IllegalArgumentException("className is not supported");
        }
    }
}
