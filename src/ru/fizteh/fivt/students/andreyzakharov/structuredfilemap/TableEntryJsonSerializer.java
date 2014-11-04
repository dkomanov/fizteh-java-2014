package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableEntryJsonSerializer implements TableEntrySerializer {
    interface Reader {
        Object getObject(String string);
    }
    interface Writer {
        String getString(Object object);
    }

    private Map<Class, Reader> readerMap = new HashMap<>();
    private Map<Class, Writer> writerMap = new HashMap<>();

    public TableEntryJsonSerializer() {
        readerMap.put(Integer.class, string -> Integer.decode(string));
        readerMap.put(Long.class,    string -> Long.decode(string));
        readerMap.put(Float.class,   string -> Float.parseFloat(string));
        readerMap.put(Double.class,  string -> Double.parseDouble(string));
        readerMap.put(Byte.class,    string -> Byte.decode(string));
        readerMap.put(Boolean.class, string -> Boolean.parseBoolean(string));
        readerMap.put(String.class,  string -> string.substring(1, string.length() - 1));

        writerMap.put(Integer.class, object -> Integer.toString((Integer)object));
        writerMap.put(Long.class,    object -> object.toString());
        writerMap.put(Float.class,   object -> object.toString());
        writerMap.put(Double.class,  object -> object.toString());
        writerMap.put(Byte.class,    object -> object.toString());
        writerMap.put(Boolean.class, object -> object.toString());
        writerMap.put(String.class,  object -> "\"" + object + "\"");
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value.charAt(0) != '[') {
            throw new ParseException("Invalid JSON", 0);
        }
        if (value.charAt(value.length() - 1) != ']') {
            throw new ParseException("Invalid JSON", value.length() - 1);
        }
        String[] tokens = value.substring(1, value.length() - 1).split(",");
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            values.add(readerMap.get(table.getColumnType(i)).getObject(tokens[i].trim()));
        }
        return new TableEntry(values);
    }

    @Override
    public String serialize(Table table, Storeable value) {
        int c = table.getColumnsCount();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < c; i++) {
            String serialized = writerMap.get(table.getColumnType(i)).getString(value.getColumnAt(i));
            sb.append(serialized);
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }
}
