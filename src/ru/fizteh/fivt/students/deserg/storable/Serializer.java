package ru.fizteh.fivt.students.deserg.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deserg on 03.12.14.
 */
public class Serializer {

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     *
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    public static Storeable deserialize(DbTable table, String value) throws ParseException {

        if (value.length() < 3
                || value.charAt(0) != '['
                || value.charAt(value.length() - 1) != ']') {
            throw new ParseException("Json format error", 0);
        }

        String[] segments = value.substring(1, value.length() - 1).split(",");
        if (segments.length != table.getColumnsCount()) {
            throw new ParseException("invalid number of columns", 0);
        }

        TableRow row = new TableRow(table.getSignature());
        for (int i = 0; i < segments.length; i++) {

            if (segments[i].isEmpty()) {
                throw new ParseException("empty column", 0);
            }

            try {
                row.setColumnAt(i, transform(table.getColumnType(i), segments[i]));
            } catch (Exception ex) {
                throw new ParseException("invalid type of column " + i, 0);
            }

        }

        return row;
    }

    /**
     * Преобразовывает объект {@link Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link Storeable}.
     * @param value {@link Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в {@link Storeable} и типа колонки в таблице.
     */
    public static String serialize(Table table, Storeable value) throws ColumnFormatException {


        StringBuilder builder = new StringBuilder("[");

        int columnNum = table.getColumnsCount();
        for (int i = 0; i < columnNum; i++) {
            Object obj = value.getColumnAt(i);
            if (obj != null && obj.getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("Serializer: serialize: invalid column format at index " + i);
            }
            if (obj == null) {
                builder.append("null");
            } else {
                builder.append(obj.toString());
            }
            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }

    public static List<Class<?>> makeSignatureFromStrings(String[] types) {

        List<Class<?>> list = new ArrayList<>();
        for (String type: types) {
            if (type.equals("int")) {
                list.add(Integer.class);
            } else if (type.equals("long")) {
                list.add(Long.class);
            } else if (type.equals("byte")) {
                list.add(Byte.class);
            } else if (type.equals("float")) {
                list.add(Float.class);
            } else if (type.equals("double")) {
                list.add(Double.class);
            } else if (type.equals("boolean")) {
                list.add(Boolean.class);
            } else if (type.equals("String")) {
                list.add(String.class);
            } else {
                throw new MyException("wrong type");
            }
        }

        return list;
    }


    private static Object transform(Class<?> classType, String pattern) {

        if (pattern.equals("null")) {
            return null;
        }

        if (classType == Integer.class) {
            return Integer.valueOf(pattern);
        }

        if (classType == Long.class) {
            return Long.valueOf(pattern);
        }

        if (classType == Byte.class) {
            return Byte.valueOf(pattern);
        }

        if (classType == Float.class) {
            return Float.valueOf(pattern);
        }

        if (classType == Double.class) {
            return Double.valueOf(pattern);
        }

        if (classType == Boolean.class) {
            return Boolean.valueOf(pattern);
        }

        if (classType == String.class) {
            return pattern;
        }

        return null;
    }

}
