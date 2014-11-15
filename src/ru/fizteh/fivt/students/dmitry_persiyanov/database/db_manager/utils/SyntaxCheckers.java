package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.utils;

/**
 * Created by Dmitry Persiyanov on 14.11.2014.
 */
public class SyntaxCheckers {
    public static boolean checkCorrectnessOfTableName(final String name) {
        return (name != null && name.split("\\s+").length == 1);
    }
}
