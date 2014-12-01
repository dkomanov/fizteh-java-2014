package ru.fizteh.fivt.students.titov.JUnit.ShellPackage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;

import static java.nio.file.FileVisitResult.*;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorDelete extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult visitFile(final Path filePath, final BasicFileAttributes attr) {
        try {
            Files.delete(filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("IOException: " + filePath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path filePath, final IOException exc) {
        System.err.println("Error occuried while visitтпg file");
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path directoryPath, final IOException exc) {
        try {
            Files.delete(directoryPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("IOException " + directoryPath.toAbsolutePath().toString());
        }
        return CONTINUE;
    }
}
