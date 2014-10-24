package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    static void rm(Path directory) throws IllegalStateException, IOException {
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files
                    .newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    rm(entry);
                }
            }
        }
        if (!directory.toFile().delete()) {
            throw new IllegalStateException("");
        }
    }
}
