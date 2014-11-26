package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Utility {
    public static void checkTableName(final String name) {
        if (name == null || name.contains(".")
                || name.contains("\\") || name.contains("/")) {
            throw new IllegalArgumentException("Table name is null or invalid");
        }
    }

    public static void checkDirectorySubdirs(Path directory) {
        File dir = directory.toFile();
        if (dir.listFiles() != null) {
            try {
                for (File subdir : dir.listFiles()) {
                    if (!subdir.isDirectory()) {
                        throw new DatabaseIOException("Subdirectories of " + directory
                                + "are not directories");
                    }
                }
            } catch (NullPointerException n) {
               throw  new DatabaseIOException("Access forbidden");
            }
        }
    }

    public static void  recursiveDelete(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                            /*
                             * Directory iteration failed.
                              */
                        throw e;
                    }
                }
            });
        } catch (IOException | SecurityException e) {
            throw new DatabaseIOException(e.getMessage());
        }
    }
}
