package ru.fizteh.fivt.students.tonmit.parallel.util;

import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public final class CastMaker {

    public static String classToString(Class<?> classArg) {
        String answer = null;
        if (classArg == Integer.class) {
            answer = "int";
        }
        if (classArg == Long.class) {
            answer = "long";
        }
        if (classArg == Byte.class) {
            answer = "byte";
        }
        if (classArg == Float.class) {
            answer = "float";
        }
        if (classArg == Double.class) {
            answer = "double";
        }
        if (classArg == Boolean.class) {
            answer = "boolean";
        }
        if (classArg == String.class) {
            answer = "String";
        }
        if (answer == null) {
            throw new IllegalArgumentException("Trying to cast unknown type to String");
        } else {
            return answer;
        }
    }

    public static Class<?> stringToClass(String className) {
        Class<?> answer;
        switch (className) {
            case "int":
                answer = Integer.class;
                break;
            case "long":
                answer = Long.class;
                break;
            case "byte":
                answer = Byte.class;
                break;
            case "float":
                answer = Float.class;
                break;
            case "double":
                answer = Double.class;
                break;
            case "boolean":
                answer = Boolean.class;
                break;
            case "String":
                answer = String.class;
                break;
            default:
                throw new IllegalArgumentException("wrong type (only primitive data types or String are allowed, \""
                        + className + "\" is not one of them)");
        }
        return  answer;
    }

    public static String makeJSON(String[] stringsWhichHoldsSignature, int firstSignificantStringIndex) {
        StringBuilder builder = new StringBuilder();
        for (int counter = firstSignificantStringIndex; counter < stringsWhichHoldsSignature.length; counter++) {
            builder.append(stringsWhichHoldsSignature[counter]).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static List<Class<?>> signatureToClassesList(String[] stringsWhichHoldsSignature,
                                                        int firstSignificantStringIndex) {
        String string = makeJSON(stringsWhichHoldsSignature, firstSignificantStringIndex);
        List<Class<?>> signature = new ArrayList<>();
        string = string.trim();
        if (!string.startsWith("(") || !string.endsWith(")")) {
            throw new RuntimeException("Incorrect signature format");
        } else if (string.contains(",")) {
            throw new RuntimeException("Types of data must not be separated by commas");
        } else {
            string = string.substring(1, string.length() - 1);
            String[] strArr = string.split(" ");
            for (String currentType : strArr) {
                signature.add(CastMaker.stringToClass(currentType));
            }
            return signature;
        }
    }

    public static Object excludeValue(String string, Class<?> type, Table table, String illegalFormatMessage)
            throws ParseException {
        string = string.trim();
        if (string.equals("null")) {
            return null;
        }
        try {
            if (type == Integer.class) {
                return Integer.parseInt(string);
            } else if (type == Long.class) {
                return Long.parseLong(string);
            } else if (type == Byte.class) {
                return Byte.parseByte(string);
            } else if (type == Float.class) {
                return Float.parseFloat(string);
            } else if (type == Double.class) {
                return Double.parseDouble(string);
            } else if (type == Boolean.class) {
                if (!"true".equals(string) && !"false".equals(string)) {
                    throw new ParseException(illegalFormatMessage + getSignatureFormat(table), 0);
                }
                return Boolean.parseBoolean(string);
            } else {
                if (!string.startsWith("\"") || !string.endsWith("\"")) {
                    throw new ParseException(illegalFormatMessage + getSignatureFormat(table), 0);
                }
                return string;
            }
        } catch (NumberFormatException e) {
            throw new ParseException(illegalFormatMessage + getSignatureFormat(table), 0);
        }
    }

    public static String getSignatureFormat(Table table) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            builder.append(CastMaker.classToString(table.getColumnType(i))).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }
}
