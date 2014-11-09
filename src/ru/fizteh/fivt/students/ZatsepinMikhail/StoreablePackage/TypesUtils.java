package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TypesUtils {
    public static HashSet<Class<?>> canonicalTypes;

    static {
        canonicalTypes.add(Integer.class);
        canonicalTypes.add(Long.class);
        canonicalTypes.add(Byte.class);
        canonicalTypes.add(Float.class);
        canonicalTypes.add(Double.class);
        canonicalTypes.add(Boolean.class);
        canonicalTypes.add(String.class);
    }

    public static boolean run(List<Class<?>> types) {
        if (types.size() != canonicalTypes.size()) {
            return false;
        }
        for (Class<?> oneClass : types) {
            if (!canonicalTypes.contains(oneClass)) {
                return false;
            }
        }
        return true;
    }

    public static boolean check(List<Class<?>> types, Storeable newValue) {
        int counter = 0;
        for (Class<?> oneType : types) {
            if (!oneType.getClass().equals(newValue.getColumnAt(counter).getClass())) {
                return false;
            }
            ++counter;
        }
        return true;
    }

    public static List<Class<?>> toTypeList(String[] types, boolean fromCommand) {
        List<Class<?>> result = new ArrayList<>();
        int start = 0;
        if (fromCommand) {
            start += 2;
        }
        for (int i = start; i < types.length; ++i) {
            switch(types[i]) {
                case "int":
                    result.add(Integer.class);
                    break;
                case "long":
                    result.add(Long.class);
                    break;
                case "byte":
                    result.add(Byte.class);
                    break;
                case "float":
                    result.add(Float.class);
                    break;
                case "double":
                    result.add(Double.class);
                    break;
                case "boolean":
                    result.add(Boolean.class);
                    break;
                case "String":
                    result.add(String.class);
                    break;
                default:
                    return null;
            }
        }
        return result;
    }

    public static String toFileSignature(List<Class<?>> types) {
        StringBuilder s = new StringBuilder();
        int counter = 0;
        for (Class<?> oneClass : types) {
            if (counter > 0) {
                s.append(" ");
            }
            switch (oneClass.toString()) {
                case "Integer":
                    s.append("int");
                    break;
                case "Byte":
                    s.append("byte");
                    break;
                case "Long":
                    s.append("long");
                    break;
                case "Boolean":
                    s.append("boolean");
                    break;
                case "Float":
                    s.append("float");
                    break;
                case "Double":
                    s.append("double");
                    break;
                case "String":
                    s.append("string");
                    break;
                default:
                    return null;
            }
            ++counter;
        }
        return s.toString();
    }
}
