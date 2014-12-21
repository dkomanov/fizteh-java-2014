package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils;

import java.nio.file.Path;

/**
 * Created by drack3800 on 23.11.2014.
 */
public class Utility {
    public static String getNameByPath(final Path path) {
        return path.getFileName().toString();
    }
}
