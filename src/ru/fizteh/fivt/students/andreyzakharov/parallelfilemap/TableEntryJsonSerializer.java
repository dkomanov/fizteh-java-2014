package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableEntryJsonSerializer implements TableEntrySerializer {
    interface Reader {
        Object getObject(String string) throws ParseException;
    }

    interface Writer {
        String getString(Object object);
    }

    private Map<Class, Reader> readerMap = new HashMap<>();
    private Map<Class, Writer> writerMap = new HashMap<>();

    public TableEntryJsonSerializer() {
        readerMap.put(Integer.class, string -> Integer.valueOf(string));
        readerMap.put(Long.class, string -> Long.valueOf(string));
        readerMap.put(Float.class, string -> Float.valueOf(string));
        readerMap.put(Double.class, string -> Double.valueOf(string));
        readerMap.put(Byte.class, string -> Byte.valueOf(string));
        readerMap.put(Boolean.class, string -> {
            if (string.trim().toLowerCase().equals("true")) {
                return true;
            } else if (string.trim().toLowerCase().equals("false")) {
                return false;
            }
            throw new ParseException("not a valid boolean value", 0);
        });
        readerMap.put(String.class, string -> {
            if (string.length() > 1 && string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
                return string.substring(1, string.length() - 1);
            }
            throw new ParseException("not a valid String value", 0);
        });

        writerMap.put(Integer.class, object -> object.toString());
        writerMap.put(Long.class, object -> object.toString());
        writerMap.put(Float.class, object -> object.toString());
        writerMap.put(Double.class, object -> object.toString());
        writerMap.put(Byte.class, object -> object.toString());
        writerMap.put(Boolean.class, object -> object.toString());
        writerMap.put(String.class, object -> "\"" + object + "\"");
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if (value.length() < 3
                || value.charAt(0) != '['
                || value.charAt(value.length() - 1) != ']'
                || value.charAt(1) == ','
                || value.charAt(value.length()-2) == ',') {
            throw new ParseException("invalid JSON", 0);
        }

        String[] tokens = value.substring(1, value.length() - 1).split(",");
        if (tokens.length != table.getColumnsCount()) {
            throw new ParseException("column count mismatch", 0);
        }
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                if (tokens[i].trim().toLowerCase().equals("null")) {
                    values.add(null);
                } else {
                    values.add(readerMap.get(table.getColumnType(i)).getObject(tokens[i].trim()));
                }
            } catch (NumberFormatException e) {
                throw new ParseException("not a valid " + MultiFileTableUtils.classToString(table.getColumnType(i))
                        + " value", 0);
            }
        }
        return new TableEntry(values);
    }

    @Override
    public String serialize(Table table, Storeable value) {
        int c = table.getColumnsCount();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < c; i++) {
            if (value.getColumnAt(i) == null) {
                sb.append("null");
            } else {
                sb.append(writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
            }
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }
}
