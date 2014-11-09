package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;

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
            if (oneType.getClass() != newValue.getColumnAt(counter).getClass()) {
                return false;
            }
            ++counter;
        }
        return true;
    }
}
