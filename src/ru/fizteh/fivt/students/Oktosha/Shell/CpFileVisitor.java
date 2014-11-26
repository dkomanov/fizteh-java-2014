package ru.fizteh.fivt.students.Oktosha.Shell;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Class which copies a directory recursively
 */

public class CpFileVisitor extends SimpleFileVisitor<Path> {
    private Path source;
    private Path target;
    CpFileVisitor(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Files.createDirectory(target.resolve(source.relativize(dir)));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, target.resolve(source.relativize(file)), REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }
}
