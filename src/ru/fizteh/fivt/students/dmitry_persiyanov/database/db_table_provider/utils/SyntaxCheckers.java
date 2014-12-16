package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils;

/**
 * Created by Dmitry Persiyanov on 14.11.2014.
 */
public class SyntaxCheckers {
    private static final String INVALID_TABLE_NAME_PATTERN =  ".*\\.|\\..*|.*(/|\\\\).*";

    public static boolean checkCorrectnessOfTableName(final String name) {
        return (name != null && !name.matches(INVALID_TABLE_NAME_PATTERN));
    }
}
