package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.utils;

import java.nio.file.Path;

public class Utility {
    public static String getNameByPath(final Path path) {
        return path.getFileName().toString();
    }
}
