package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;

import static java.nio.file.FileVisitResult.*;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorCopy extends SimpleFileVisitor<Path> {
    private final Path startPath;
    private final Path destinationPath;

    public FileVisitorCopy(final Path newStartPath, final Path newDestinationPath) {
        startPath = newStartPath;
        destinationPath = newDestinationPath;
    }

    @Override
    public FileVisitResult visitFile(final Path filePath, final BasicFileAttributes attr) {
        Path newFilePath = destinationPath.resolve(startPath.relativize(filePath));
        try {
            Files.copy(filePath, newFilePath);
        } catch (IOException e) {
            System.err.println("IOException: " + filePath.toAbsolutePath());
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path filePath, IOException exc) {
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dirPath, BasicFileAttributes attr) {
        Path newDirPath = destinationPath.resolve(startPath.relativize(dirPath));
        try {
            Files.createDirectory(newDirPath);
        } catch (IOException e) {
            System.err.println("IOException");
        }
        return CONTINUE;
    }
}
