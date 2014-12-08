package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import java.text.ParseException;
import java.util.*;

/**
 * Created by Мирон on 09.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class Parser {
    interface Read {
        Object getObject(String string) throws ParseException;
    }
    interface Write {
        String getString(Object object);
    }
    private Map<Class, Read> readMap = new HashMap<>();
    private Map<Class, Write> writeMap = new HashMap<>();

    public Parser() {

        readMap.put(Integer.class, (String s) -> Integer.valueOf(s));

        readMap.put(Long.class, (String s) -> Long.valueOf(s));

        readMap.put(Float.class, (String s) -> Float.valueOf(s));

        readMap.put(Double.class, (String s) -> Double.valueOf(s));

        readMap.put(Byte.class, (String s) -> Byte.valueOf(s));

        readMap.put(Boolean.class, (String s) -> {
                if (s.trim().toLowerCase().equals("true")) {
                    return true;
                } else if (s.trim().toLowerCase().equals("false")) {
                    return false;
                }
                throw new ParseException("not valid boolean value", 0);
            });

        readMap.put(String.class, (String s) -> {
                if (s.length() > 1 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
                throw new ParseException("not valid String value", 0);
            });

        ArrayList<Class> classes = new ArrayList<>(Arrays.asList(Integer.class, Long.class,
                Float.class, Double.class, Byte.class, Boolean.class));

        for (Class c : classes) {
            writeMap.put(c, new Write() {
                @Override
                public String getString(Object object) {
                    return object.toString();
                }
            });
        }

        writeMap.put(String.class, new Write() {
            @Override
            public String getString(Object object) {
                return "\"" + object + "\"";
            }
        });

    }


    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if (value.length() < 3 || value.charAt(0) != '[' || value.charAt(value.length() - 1) != ']'
                || value.charAt(1) == ',' || value.charAt(value.length() - 2) == ',') {
            throw new ParseException("JSON is invalid, failed with '[' or ','", 0);
        }
        String[] tokens = value.substring(1, value.length() - 1).split(",");
        if (tokens.length != table.getColumnsCount()) {
            throw new ParseException("wrong number of columns", 0);
        }
        List<Object> contents = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                if (!("null".equals(tokens[i].trim().toLowerCase()))) {
                    contents.add(readMap.get(table.getColumnType(i)).getObject(tokens[i].trim()));
                } else {
                    contents.add(null);
                }
            } catch (NumberFormatException e) {
                throw new ParseException("not valid ", 0);
            }
        }
        return new CStoreable(contents);
    }

    public String serialize(Table table, Storeable value) {
        int columnsCount = table.getColumnsCount();
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < columnsCount; i++) {
            if (value.getColumnAt(i) != null) {
                stringBuilder.append(writeMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            } else {
                stringBuilder.append("null");
            }
            stringBuilder.append(',');
        }
        stringBuilder.setCharAt(stringBuilder.length() - 1, ']');
        return stringBuilder.toString();
    }

}
