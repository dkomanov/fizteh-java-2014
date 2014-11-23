package ru.fizteh.fivt.students.SurkovaEkaterina.Storable;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem.TableInfo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StorableUsage {
    public static TableInfo parseCreateCommand(String parameters) throws IllegalArgumentException {
        parameters = parameters.trim().replaceAll("\\s+", " ");
        String tableName = parameters.split("\\s+")[0];
        
        int spaceIndex = parameters.indexOf(' ');
        
        if (spaceIndex == -1) {
            throw new IllegalArgumentException("Wrong type (no column types)!");
        }
        
        String columnTypesString = parameters.substring(spaceIndex).replaceAll("\\((.*)\\)", "$1");
        String[] columnTypesArray = CommandsParser.parseCommandParameters(columnTypesString);
        List<String> columnTypes = new ArrayList<String>();

        for (String str : columnTypesArray) {
            columnTypes.add(str);
        }

        TableInfo info = new TableInfo(tableName);
        for (final String columnType : columnTypes) {
            info.addColumn(TypesParser.getTypeByName(columnType));
        }

        return info;
    }

    public static String concatenateListEntries(List<?> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (final Object listElement : list) {
            if (!first) {
                sb.append(" ");
            }
            first = false;
            if (listElement == null) {
                sb.append("null");
            } else {
                sb.append(listElement.toString());
            }
        }
        return sb.toString();
    }

    public static String parseTableName(String parameters) {
        int index = parameters.indexOf(' ');
        
        if (index == -1) {
            return parameters;
        }
        
        return parameters.substring(0, index);
    }

    public static List<String> formatColumnTypes(List<Class<?>> columnTypes) {
        List<String> newColumnTypes = new ArrayList<String>();
        
        for (final Class<?> columnType : columnTypes) {
            newColumnTypes.add(TypesParser.getNameByType(columnType));
        }
        return newColumnTypes;
    }

    public static void checkValue(Object value, Class<?> type) throws ParseException {
        if (value == null) {
            return;
        }
        if (value.toString().trim().equals("")) {
            throw new ParseException("Value can not be empty!", 0);
        }
        if (value.getClass() != type) {
            throw new ParseException("Value type is not equal to expected!", 0);
        }
    }
}
