package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Aliaksei Semchankau on 19.11.2014.
 */
public class SerializeFunctions {

    private static HashMap<String, Class<?>> stringToClassMap = new HashMap<String, Class<?>>();
    private static HashMap<Class<?>, String> classToStringMap = new HashMap<Class<?>, String>();

    static {

        stringToClassMap.put("int", Integer.class);
        stringToClassMap.put("long", Long.class);
        stringToClassMap.put("byte", Byte.class);
        stringToClassMap.put("float", Float.class);
        stringToClassMap.put("double", Double.class);
        stringToClassMap.put("boolean", Boolean.class);
        stringToClassMap.put("String", String.class);

        classToStringMap.put(Integer.class, "int");
        classToStringMap.put(Long.class, "long");
        classToStringMap.put(Byte.class, "byte");
        classToStringMap.put(Float.class, "float");
        classToStringMap.put(Double.class, "double");
        classToStringMap.put(Boolean.class, "boolean");
        classToStringMap.put(String.class, "String");


    }

    interface TransformToObject {
        Object getObject(String s) throws ParseException;
    }

    public Class<?> toClass(String stringToClass) {
        return stringToClassMap.get(stringToClass);
    }

    public String toString(Class<?> classToString) {
        return classToStringMap.get(classToString);
    }

    public DatabaseStoreable deserialize(Table table, String jSON) throws ParseException {
        ArrayList<Class<?>> signature = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            signature.add(table.getColumnType(i));
        }
        return deserialize(signature, jSON);
    }

    public DatabaseStoreable deserialize(ArrayList<Class<?>> signature, String jSON) throws ParseException {

        jSON = jSON.trim();
        if (jSON.length() < 3 || jSON.charAt(0) != '[' || jSON.charAt(jSON.length() - 1) != ']') {
            throw new ParseException("can't parse " + jSON.toString(), 0);
        }

        List<Object> storeValues = new ArrayList<>();

        String[] elements = jSON.substring(1, jSON.length() - 1).split(",");

        if (elements.length != signature.size()) {
            throw new ParseException("incorrect size in " + jSON + "; need " + signature.size(), 0);
        }

        for (int i = 0; i < elements.length; ++i) {
            String currentElement = elements[i];
            //System.out.println(currentElement);
            currentElement = currentElement.trim();
            //System.out.println(currentElement);
            if (currentElement.equals("null")) {
                storeValues.add(null);
            } else {
                try {
                    storeValues.add(transformToClass(signature.get(i), currentElement));
                } catch (Exception exc) {
                    throw new ParseException(currentElement + " isn't good for " + signature.get(i).toString(), 0);
                }
            }
        }

        return new DatabaseStoreable(storeValues);
    }

    public String serialize(Table dTable, Storeable store) throws ParseException {



        StringBuilder jSON = new StringBuilder();
        jSON.append("[");
        for (int i = 0; i < dTable.getColumnsCount(); ++i) {
            Object object = store.getColumnAt(i);
            if (object == null) {
                jSON.append("null");
            } else {
                jSON.append(transformToString(object));
            }
            if (i < dTable.getColumnsCount() - 1) {
                jSON.append(", ");
            }
        }
        jSON.append("]");
        return jSON.toString();
    }

    public Object transformToClass(Class<?> unknownClass, String currentElement) throws ParseException {
        if (unknownClass.equals(Integer.class)) {
            return Integer.valueOf(currentElement);
        }
        if (unknownClass.equals(Long.class)) {
            return Long.valueOf(currentElement);
        }
        if (unknownClass.equals(Byte.class)) {
            return Byte.valueOf(currentElement);
        }
        if (unknownClass.equals(Float.class)) {
            return Float.valueOf(currentElement);
        }
        if (unknownClass.equals(Double.class)) {
            return Double.valueOf(currentElement);
        }
        if (unknownClass.equals(Boolean.class)) {
            if (currentElement.toLowerCase().equals("true")) {
                return true;
            }
            if (currentElement.toLowerCase().equals("false")) {
                return false;
            }
            throw new ParseException("incorrect value for Boolean", 0);
        }
        if (unknownClass.equals(String.class)) {
            if (currentElement.length() < 3 || currentElement.charAt(0) != '"'
                    || currentElement.charAt(currentElement.length() - 1) != '"') {
                throw new ParseException("incorrect value for String", 0);
            }
            return currentElement.substring(1, currentElement.length() - 1);
        }
        return null;
    }

    public String transformToString(Object object) {
        if (object.getClass() == Integer.class) {
            return Integer.toString((Integer) object);
        }
        if (object.getClass() == Long.class) {
            return Long.toString((Long) object);
        }
        if (object.getClass() == Byte.class) {
            return Byte.toString((Byte) object);
        }
        if (object.getClass() == Float.class) {
            return Float.toString((Float) object);
        }
        if (object.getClass() == Double.class) {
            return Double.toString((Double) object);
        }
        if (object.getClass() == Boolean.class) {
            return Boolean.toString((Boolean) object);
        }
        if (object.getClass() == String.class) {
            String toReturn = (String) object;
            toReturn = "\"" + toReturn + "\"";
            return toReturn;
        }
        return null;
    }

}
