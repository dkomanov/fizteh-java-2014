package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;

public class JSONParseException extends ParseException {

    public JSONParseException(String string) {
        super("Format error: " + string, 0);
    }

    public JSONParseException(String string, int index) {
        super("Format error: " + string, index);
    }

    public JSONParseException(String string, Table table) {
        super(buildMsg(string, table), 0);
    }

    private static String buildMsg(String string, Table table) {
        StringBuilder error = new StringBuilder("Format error: ");
        error.append(string);
        error.append(". Expected: ");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            error.append(table.getColumnType(i).getSimpleName());
            error.append(' ');
        }

        return error.deleteCharAt(error.length() - 1).toString();
    }
}
