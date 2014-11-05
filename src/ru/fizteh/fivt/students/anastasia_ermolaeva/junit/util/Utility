package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

import java.io.File;
import java.nio.file.Path;

public class Utility {
    public static void checkTableName(final String name) {
        if (name == null || name.contains(".")
                || name.contains("\\") || name.contains("/"))
            throw new IllegalArgumentException("Table name is null or invalid");
    }

    public static void checkDirectorySubdirs(Path directory) {
        File dir = directory.toFile();
        if (dir.listFiles() != null) {
            for (File subdir : dir.listFiles()) {
                if (!subdir.isDirectory()) {
                    throw new DatabaseIOException("Subdirectories of " + directory
                            + "are not directories");
                }
            }
        }
    }
}
