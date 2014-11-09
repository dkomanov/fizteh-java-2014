package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

import java.text.ParseException;
import java.util.HashMap;

public class Serializator {
    interface GetSmthSer {
        Object get(int index);
    }

    interface GetSmthDeser {
        Object get(String s) throws IllegalArgumentException;
    }


    private static HashMap<Class<?>, GetSmthSer> toFindApproriateSer;
    private static HashMap<Class<?>, GetSmthDeser> toFindApproriateDeser;
    static {
        Storeable s = new AbstractStoreable(null);
        toFindApproriateSer.put(Integer.class, s::getIntAt);
        toFindApproriateSer.put(Long.class, s::getLongAt);
        toFindApproriateSer.put(Byte.class, s::getByteAt);
        toFindApproriateSer.put(Float.class, s::getFloatAt);
        toFindApproriateSer.put(Boolean.class, s::getBooleanAt);
        toFindApproriateSer.put(String.class, s::getStringAt);

        toFindApproriateDeser.put(Integer.class, Integer::parseInt);
        toFindApproriateDeser.put(Long.class, Long::parseLong);
        toFindApproriateDeser.put(Byte.class, Byte::parseByte);
        toFindApproriateDeser.put(Float.class, Float::parseFloat);
        toFindApproriateDeser.put(Double.class, Double::parseDouble);
        toFindApproriateDeser.put(Boolean.class, Boolean::parseBoolean);
        toFindApproriateDeser.put(String.class, String::toString);
    }

    public static String serialize(Table table, Storeable value) {
        StringBuilder s = new StringBuilder();
        s.append("<row>");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            s.append("<col>");
            if (value.getColumnAt(i) == null) {
                s.append("<null/>");
            } else {
                s.append(toFindApproriateSer.get(table.getColumnType(i).getClass()).get(i));
            }
            s.append("</col>");
        }
        s.append("</row>");
        return s.toString();
    }

    public static Storeable deserialize(Table table, String valueXML) throws ParseException {
        String[] entries = valueXML.split("<row>.*</row>");
        if (entries.length < 0 || entries.length >= 2) {
            throw new ParseException(valueXML, -2);
        }

        String[] fields = entries[0].split("<col>.*</col>");
        if (fields.length != table.getColumnsCount()) {
            throw new ParseException(valueXML, -1);
        }
        FileMap one = (FileMap) table;
        Storeable result = one.getTableProvider().createFor(table);
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].equals("<null/>")) {
                result.setColumnAt(i, null);
            } else {
                try {
                    result.setColumnAt(i, toFindApproriateDeser.get(table.getColumnType(i)).get(fields[i]));
                } catch (IllegalArgumentException e) {
                    throw new ParseException(fields[i], i);
                }
            }
        }
        return result;
    }
}
