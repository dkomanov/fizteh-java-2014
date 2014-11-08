package ru.fizteh.fivt.students.gudkov394.Storable.src;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kagudkov on 07.11.14.
 */
public class MySerialize implements Serializable {

    interface Read {
        Object getObject(String string) throws ParseException;
    }

    interface Write {
        String getString(Object object);
    }

    private Map<Class, Read> ReadMap = new HashMap<>();
    private Map<Class, Write> WriteMap = new HashMap<>();

    public MySerialize() {
        ReadMap.put(Integer.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                return Integer.valueOf(string);
            }
        });

        ReadMap.put(Long.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                return Long.valueOf(string);
            }
        });

        ReadMap.put(Float.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                return Float.valueOf(string);
            }
        });

        ReadMap.put(Double.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                return Double.valueOf(string);
            }
        });

        ReadMap.put(Byte.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                return Byte.valueOf(string);
            }
        });

        ReadMap.put(Boolean.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                if (string.trim().toLowerCase().equals("true")) {
                    return true;
                } else if (string.trim().toLowerCase().equals("false")) {
                    return false;
                }
                throw new ParseException("not a valid boolean value", 0);
            }
        });

        ReadMap.put(String.class, new Read() {
            @Override
            public Object getObject(String string) throws ParseException {
                if (string.length() > 1 && string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
                    return string.substring(1, string.length() - 1);
                }
                throw new ParseException("not a valid String value", 0);
            }
        });

        WriteMap.put(Integer.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(Long.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(Float.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(Double.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(Byte.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(Boolean.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        WriteMap.put(String.class, new Write() {
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
            throw new ParseException("wrong number of column", 0);
        }
        List<Object> contents = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                if (!("null".equals(tokens[i].trim().toLowerCase()))) {
                    contents.add(ReadMap.get(table.getColumnType(i)).getObject(tokens[i].trim()));
                } else {
                    contents.add(null);
                }
            } catch (NumberFormatException e) {
                throw new ParseException("not a valid ", 0);
            }
        }
        return new tableContents(contents);
    }

    public String serialize(Table table, Storeable value) {
        int columnsCount = table.getColumnsCount();
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < columnsCount; i++) {
            if (value.getColumnAt(i) != null) {
                stringBuilder.append(WriteMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            } else {
                stringBuilder.append("null");
            }
            stringBuilder.append(',');
        }
        stringBuilder.setCharAt(stringBuilder.length() - 1, ']');
        return stringBuilder.toString();
    }
}
