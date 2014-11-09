package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CheckTypesValidity {
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

    public static List<Class<?>> toTypeList(String[] types) {
        List<Class<?>> result = new ArrayList<>();
        for (String oneType : types) {
            switch(oneType) {
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
}
