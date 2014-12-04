package ru.fizteh.fivt.students.deserg.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;

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
    public static Storeable deserialize(Table table, String value) throws ParseException {

        if (value.length() < 3
                || value.charAt(0) != '['
                || value.charAt(value.length() - 1) != '[') {
            throw new ParseException("Serializer: deserialize: parse error", 0);
        }

        String[] segments = value.substring(1, value.length() - 2).split(",");
        for (String segment: segments) {

            if (segment.isEmpty()) {
                throw new ParseException("Serializer: deserialize: parse error", 0);
            }

            

        }



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
            builder.append(value.getColumnAt(i));
            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }


}
