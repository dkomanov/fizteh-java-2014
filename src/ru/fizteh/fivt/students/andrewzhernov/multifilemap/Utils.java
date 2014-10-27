package ru.fizteh.fivt.students.andrewzhernov.multifilemap;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    static void remove(Path directory) throws Exception {
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    remove(entry);
                }
            }
        }
        if (!directory.toFile().delete()) {
            throw new Exception("Cannot delete " + directory.toString());
        }
    }
}
