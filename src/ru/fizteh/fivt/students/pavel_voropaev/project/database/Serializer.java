package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.Parser;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.JSONParseException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.NullArgumentException;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Serializer {
    static final String SIGNATURE_FILE_NAME = "signature.tsv";

    public static final Map<String, Class<?>> SUPPORTED_TYPES;

    static {
        Map<String, Class<?>> supportedTypes = new HashMap<>();
        supportedTypes.put("int", Integer.class);
        supportedTypes.put("long", Long.class);
        supportedTypes.put("byte", Byte.class);
        supportedTypes.put("float", Float.class);
        supportedTypes.put("double", Double.class);
        supportedTypes.put("boolean", Boolean.class);
        supportedTypes.put("String", String.class);
        SUPPORTED_TYPES = Collections.unmodifiableMap(supportedTypes);
    }

    static final Map<Class<?>, String> TYPES_TO_NAMES;

    static {
        Map<Class<?>, String> supportedTypes = new HashMap<>();
        supportedTypes.put(Integer.class, "int");
        supportedTypes.put(Long.class, "long");
        supportedTypes.put(Byte.class, "byte");
        supportedTypes.put(Float.class, "float");
        supportedTypes.put(Double.class, "double");
        supportedTypes.put(Boolean.class, "boolean");
        supportedTypes.put(String.class, "String");
        TYPES_TO_NAMES = Collections.unmodifiableMap(supportedTypes);
    }

    static final Map<Class<?>, Function<String, Object>> PARSER_METHODS;

    static {
        Map<Class<?>, Function<String, Object>> map = new HashMap<>();
        map.put(Integer.class, Integer::parseInt);
        map.put(Long.class, Long::parseLong);
        map.put(Byte.class, Byte::parseByte);
        map.put(Float.class, Float::parseFloat);
        map.put(Double.class, Double::parseDouble);
        map.put(Boolean.class, string -> {
            if (!string.matches("(?i)true|false")) {
                throw new ColumnFormatException("Expected 'true' or 'false'");
            }
            return Boolean.parseBoolean(string);
        });
        map.put(String.class, string -> {
            if (string.charAt(0) != '"' || string.charAt(string.length() - 1) != '"') {
                throw new ColumnFormatException("Expected \"<string>\" ");
            }
            return string.substring(1, string.length() - 1);
        });
        PARSER_METHODS = Collections.unmodifiableMap(map);
    }

    public static void deserialize(Table table, Storeable storeable, Parser parser) throws ParseException {
        int currentColumn;
        for (currentColumn = 0; true; ++currentColumn) {
            String elementStr = parser.getNext();
            if (elementStr == null) {
                break;
            }

            if ("null".equals(elementStr)) {
                storeable.setColumnAt(currentColumn, null);
            } else {
                try {
                    storeable.setColumnAt(currentColumn,
                            Serializer.PARSER_METHODS.get(table.getColumnType(currentColumn)).apply(elementStr));
                } catch (NumberFormatException | ColumnFormatException e) {
                    throw new JSONParseException(
                            elementStr + " cannot be parsed as " + table.getColumnType(currentColumn).getSimpleName(),
                            table);
                } catch (IndexOutOfBoundsException e) {
                    throw new JSONParseException("Too many columns", table);
                }
            }
        }

        if (currentColumn != table.getColumnsCount()) {
            throw new JSONParseException("Found " + currentColumn + " columns. Expected: " + table.getColumnsCount(),
                    table);
        }
    }

    public static String serialize(Table table, Storeable value, char delim, char escape) throws ColumnFormatException {
        if (table == null || value == null) {
            throw new NullArgumentException("deserialize");
        }

        StringBuilder serialized = new StringBuilder();
        Object object;
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            object = value.getColumnAt(i);
            if (object != null && object.getClass().equals(String.class)) {
                serialized.append(escape);
            }
            serialized.append(object);
            if (object != null && object.getClass().equals(String.class)) {
                serialized.append(escape);
            }
            if (i != table.getColumnsCount() - 1) {
                serialized.append(delim);
                if (!Character.isSpaceChar(delim)) {
                    serialized.append(' ');
                }
            }
        }

        return serialized.toString();
    }
}
