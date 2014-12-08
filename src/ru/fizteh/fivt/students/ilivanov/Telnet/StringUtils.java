package ru.fizteh.fivt.students.ilivanov.Telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static String readAnswer(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        char[] input = new char[1024];
        String end = System.lineSeparator() + " $ ";
        do {
            int k = reader.read(input, 0, 1024);
            stringBuilder = stringBuilder.append(input, 0, k);
        } while (!stringBuilder.toString().endsWith(end));
        return stringBuilder.delete(stringBuilder.length() - end.length(), stringBuilder.length()).toString();
    }

    public static String readLine(BufferedReader reader) throws IOException {
        String message = reader.readLine();
        if (message == null) {
            throw new IOException("EOF reached");
        }
        return message;
    }

    public static ArrayList<Class<?>> stringToClassList(String types) {
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        String[] typeNames = types.split("\\s+");
        for (int i = 0; i < typeNames.length; i++) {
            if (typeNames[i].equals("int")) {
                columnTypes.add(Integer.class);
            } else if (typeNames[i].equals("long")) {
                columnTypes.add(Long.class);
            } else if (typeNames[i].equals("byte")) {
                columnTypes.add(Byte.class);
            } else if (typeNames[i].equals("float")) {
                columnTypes.add(Float.class);
            } else if (typeNames[i].equals("double")) {
                columnTypes.add(Double.class);
            } else if (typeNames[i].equals("boolean")) {
                columnTypes.add(Boolean.class);
            } else if (typeNames[i].equals("String")) {
                columnTypes.add(String.class);
            } else {
                throw new RuntimeException(String.format("Unknown type %s", typeNames[i]));
            }
        }
        return columnTypes;
    }

    public static String classListToString(List<Class<?>> columnTypes) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < columnTypes.size(); i++) {
            if (columnTypes.get(i) == Integer.class) {
                result.append("int");
            } else if (columnTypes.get(i) == Long.class) {
                result.append("long");
            } else if (columnTypes.get(i) == Byte.class) {
                result.append("byte");
            } else if (columnTypes.get(i) == Float.class) {
                result.append("float");
            } else if (columnTypes.get(i) == Double.class) {
                result.append("double");
            } else if (columnTypes.get(i) == Boolean.class) {
                result.append("boolean");
            } else if (columnTypes.get(i) == String.class) {
                result.append("String");
            }
            if (i != columnTypes.size() - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }
}
