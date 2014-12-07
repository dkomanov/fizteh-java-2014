package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;

import static java.nio.file.FileVisitResult.*;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorDelete extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult visitFile(final Path filePath, final BasicFileAttributes attr) throws IOException {
        Files.delete(filePath.toAbsolutePath());
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path filePath, final IOException exc) {
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path directoryPath, final IOException exc) throws IOException {
        Files.delete(directoryPath.toAbsolutePath());
        return CONTINUE;
    }
}
