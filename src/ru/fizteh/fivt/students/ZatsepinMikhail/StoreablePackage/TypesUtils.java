package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TypesUtils {
    public static HashSet<Class<?>> canonicalTypes;

    static {
        canonicalTypes = new HashSet<>();
        canonicalTypes.add(Integer.class);
        canonicalTypes.add(Long.class);
        canonicalTypes.add(Byte.class);
        canonicalTypes.add(Float.class);
        canonicalTypes.add(Double.class);
        canonicalTypes.add(Boolean.class);
        canonicalTypes.add(String.class);
    }

    public static boolean checkTypes(List<Class<?>> types) {
        for (Class<?> oneClass : types) {
            if (!canonicalTypes.contains(oneClass)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkNewStorableValue(List<Class<?>> types, Storeable newValue)
    throws IndexOutOfBoundsException, IndexOutOfBoundsException {
        if (types.size() != TypesUtils.getSizeOfStoreable(newValue)) {
            throw new IndexOutOfBoundsException();
        }
        List<Object> valueList = new ArrayList<>();
        for (int i = 0; i < types.size(); ++i) {
            valueList.add(newValue.getColumnAt(i));
        }
        return checkNewStorableValue(types, valueList);
    }

    public static boolean checkNewStorableValue(List<Class<?>> types, List<Object> newValue)
    throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() != newValue.size()) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < types.size(); ++i) {
            if (newValue.get(i) != null
                    && !newValue.get(i).getClass().equals(types.get(i))) {
                throw new ColumnFormatException(newValue.get(i).getClass().toString());
            }
        }
        return true;
    }

    public static List<Class<?>> toTypeList(String[] types) throws ColumnFormatException {
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
                    throw new ColumnFormatException("wrong type: " + oneType);
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
            switch (oneClass.getTypeName()) {
                case "java.lang.Integer":
                    s.append("int");
                    break;
                case "java.lang.Byte":
                    s.append("byte");
                    break;
                case "java.lang.Long":
                    s.append("long");
                    break;
                case "java.lang.Boolean":
                    s.append("boolean");
                    break;
                case "java.lang.Float":
                    s.append("float");
                    break;
                case "java.lang.Double":
                    s.append("double");
                    break;
                case "java.lang.String":
                    s.append("String");
                    break;
                default:
                    return null;
            }
            ++counter;
        }
        return s.toString();
    }


    public static int getSizeOfStoreable(Storeable obj) {
        for (int i = 0; ; ++i) {
            try {
                obj.getColumnAt(i);
            } catch (IndexOutOfBoundsException e) {
                return i;
            }
        }
    }
}
