package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyException;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.TableRow;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by deserg on 03.12.14.
 */
public class Serializer {

    interface Transformer {
        Object getObject(String string) throws ParseException;
    }
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

        String[] segments = value.substring(1, value.length() - 1).split(",\\s*");
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
                throw new ColumnFormatException("Serializer: serialize: invalid column format at index " + (i + 1));
            }
            if (obj == null) {
                builder.append("null");
            } else if (obj.getClass() == String.class) {
                builder.append("\"").append(obj).append("\"");
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

        if (types == null) {
            throw new MyException("null array of types");
        }

        List<Class<?>> list = new ArrayList<>();
        for (String type: types) {

            StoreableMap map;

            try {
                map = StoreableMap.getFromString(type);
            } catch (EnumConstantNotPresentException ex) {
                throw new MyException("wrong type");
            }

            list.add(map.getBoxedClass());
        }

        return list;
    }

    public static String makeStringFromSignature(List<Class<?>> signature) {

        if (signature == null) {
            return "null";
        }

        String result = "(";
        for (Class<?> cl: signature) {

            StoreableMap map;

            try {
                map = StoreableMap.getFromClass(cl);
                result += map.getString() + " ";
            } catch (EnumConstantNotPresentException ex) {
                return "wrong type: " + cl.getSimpleName();
            }
        }

        return result.substring(0, result.length() - 1) + ")";

    }


    private static Object transform(Class<?> classType, String pattern) throws ParseException {

        if (pattern.equals("null")) {
            return null;
        }

        Map<Class, Transformer> map = new HashMap<>();
        map.put(Integer.class, Integer::valueOf);
        map.put(Long.class, Long::valueOf);
        map.put(Byte.class, Byte::valueOf);
        map.put(Float.class, Float::valueOf);
        map.put(Double.class, Double::valueOf);
        map.put(Boolean.class, Boolean::valueOf);
        map.put(String.class, string -> string.substring(1, string.length() - 1));

        return map.get(classType).getObject(pattern);
    }

}
