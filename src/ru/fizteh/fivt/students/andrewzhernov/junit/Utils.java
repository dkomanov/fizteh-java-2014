package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

public class Utils {
    public static void removeDir(Path directory) throws IllegalStateException {
        try {
            if (Files.isDirectory(directory)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                    for (Path entry : stream) {
                        removeDir(entry);
                    }
                }
            }
            if (!directory.toFile().delete()) {
                throw new IllegalStateException("Can't remove " + directory.toString());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
