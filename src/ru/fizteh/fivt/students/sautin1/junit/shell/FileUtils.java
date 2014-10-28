package ru.fizteh.fivt.students.sautin1.junit.shell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sautin1 on 10/20/14.
 */
public class FileUtils {

    public static List<Path> listFiles(Path dirPath) throws IOException {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new FileNotFoundException(dirPath.toString());
        }
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : directoryStream) {
                if (!Files.isHidden(entry)) {
                    paths.add(entry);
                }
            }
        }
        return paths;
    }

    public static void removeRecursively(Path dirPath) throws IOException {
        if (!Files.isDirectory(dirPath)) {
            Files.delete(dirPath);
        } else {
            List<Path> paths = listFiles(dirPath);
            for (Path entry : paths) {
                removeRecursively(entry);
            }
            Files.delete(dirPath);
        }
    }

    public static void removeDirectory(Path dirPath) throws IOException {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new FileNotFoundException(dirPath.toString());
        }
        removeRecursively(dirPath);
    }

    public static void clearDirectory(Path dirPath) throws IOException {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new FileNotFoundException(dirPath.getFileName().toString());
        }
        List<Path> paths = listFiles(dirPath);
        for (Path entry : paths) {
            removeRecursively(entry);
        }
    }
}
