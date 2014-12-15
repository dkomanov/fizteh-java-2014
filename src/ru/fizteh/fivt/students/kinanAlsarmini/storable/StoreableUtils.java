package ru.fizteh.fivt.students.kinanAlsarmini.storable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.TableInfo;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.xml.XmlDeserializer;

public class StoreableUtils {
    public static List<Object> parseValues(List<String> valuesRepresentation, Table table) throws ColumnFormatException {
        // values start from index 1
        List<Object> values = new ArrayList<>(valuesRepresentation.size() - 1);

        for (int index = 1; index < valuesRepresentation.size(); ++index) {
            Object value = TypesFormatter.parseByClass(valuesRepresentation.get(index), table.getColumnType(index - 1));

            values.add(value);
        }

        return values;
    }

    public static String join(List<?> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (final Object listEntry : list) {
            if (!first) {
                sb.append(" ");
            }

            first = false;

            if (listEntry == null) {
                sb.append("null");
            } else {
                sb.append(listEntry.toString());
            }
        }

        return sb.toString();
    }

    public static TableInfo parseCreateCommand(String parameters) throws IllegalArgumentException {
        parameters = parameters.trim();

        List<String> params = new ArrayList<String>();
        String tableName = parameters.split("\\s+")[0];

        parameters = parameters.replaceAll("\\s+", " ");

        int spaceIndex = parameters.indexOf(' ');

        if (spaceIndex == -1) {
            throw new IllegalArgumentException("wrong type (no column types)");
        }

        String columnTypesString = parameters.substring(spaceIndex).replaceAll("\\((.*)\\)", "$1");
        List<String> columnTypes = CommandParser.parseParams(columnTypesString);

        TableInfo info = new TableInfo(tableName);

        for (final String columnType : columnTypes) {
            info.addColumn(TypesFormatter.getTypeByName(columnType));
        }

        return info;
    }

    public static List<String> formatColumnTypes(List<Class<?>> columnTypes) {
        List<String> formattedColumnTypes = new ArrayList<String>();

        for (final Class<?> columnType : columnTypes) {
            formattedColumnTypes.add(TypesFormatter.getSimpleName(columnType));
        }

        return formattedColumnTypes;
    }

    public static void checkValue(Object value, Class<?> type) throws ParseException {
        if (value == null) {
            return;
        }

        switch (TypesFormatter.getSimpleName(type)) {
            case "String":
                String stringValue = (String) value;
                if (checkStringCorrect(stringValue)) {
                    throw new ParseException("value cannot be null", 0);
                }
                break;
        }
    }

    public static boolean checkStringCorrect(String string) {
        return string.matches("\\s*") || string.split("\\s+").length != 1;
    }
}
