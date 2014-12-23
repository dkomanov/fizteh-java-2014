package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table_provider.utils;

public class SyntaxCheckers {
    private static final String INVALID_TABLE_NAME_PATTERN =  ".*\\.|\\..*|.*(/|\\\\).*";

    public static boolean checkCorrectnessOfTableName(final String name) {
        return (name != null && !name.matches(INVALID_TABLE_NAME_PATTERN));
    }
}
