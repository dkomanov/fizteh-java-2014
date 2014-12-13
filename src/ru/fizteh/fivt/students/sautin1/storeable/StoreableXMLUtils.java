package ru.fizteh.fivt.students.sautin1.storeable;

import java.text.ParseException;
import java.util.*;

/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableXMLUtils {
    public static final String ROW_START_TAG = "row";
    public static final String COLUMN_START_TAG = "col";
    public static final String EMPTY_TAG = "null";
    public static final String SIGNATURE_CLASS_DELIMITER = "\\s+";
    public static final Set<Class<?>> TYPE_SET;
    public static final Map<String, Class<?>> CLASS_NAME_MAP;

    static {
        TYPE_SET = new HashSet<>();
        TYPE_SET.add(int.class);
        TYPE_SET.add(Integer.class);
        TYPE_SET.add(long.class);
        TYPE_SET.add(Long.class);
        TYPE_SET.add(byte.class);
        TYPE_SET.add(Byte.class);
        TYPE_SET.add(float.class);
        TYPE_SET.add(Float.class);
        TYPE_SET.add(double.class);
        TYPE_SET.add(Double.class);
        TYPE_SET.add(boolean.class);
        TYPE_SET.add(Boolean.class);
        TYPE_SET.add(String.class);

        CLASS_NAME_MAP = new HashMap<>();
        CLASS_NAME_MAP.put("int", Integer.class);
        CLASS_NAME_MAP.put("long", Long.class);
        CLASS_NAME_MAP.put("byte", Byte.class);
        CLASS_NAME_MAP.put("float", Float.class);
        CLASS_NAME_MAP.put("double", Double.class);
        CLASS_NAME_MAP.put("boolean", Boolean.class);
        CLASS_NAME_MAP.put("String", String.class);
    }

    public static String buildSignatureString(List<Class<?>> valueTypes) {
        String signature = "";
        for (Class<?> valueType : valueTypes) {
            String name = valueType.getSimpleName();
            if (!name.equals("String")) {
                name = name.toLowerCase();
            }
            if (name.equals("integer")) {
                name = "int";
            }
            signature += name;
            signature += ' ';
        }
        signature = signature.trim();
        return signature;
    }


    public static List<Class<?>> parseSignatureString(String signature) throws ParseException {
        List<Class<?>> valueTypes = new ArrayList<>();
        String[] classNames = signature.split(SIGNATURE_CLASS_DELIMITER);
        for (String className : classNames) {
            Class<?> newClass = CLASS_NAME_MAP.get(className);
            if (newClass == null) {
                throw new ParseException("Wrong signature string", 0);
            }
            valueTypes.add(newClass);
        }
        return valueTypes;
    }
}
