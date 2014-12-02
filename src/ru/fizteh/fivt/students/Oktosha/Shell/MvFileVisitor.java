package ru.fizteh.fivt.students.Oktosha.Shell;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Class which moves a directory recursively
 */

public class MvFileVisitor extends SimpleFileVisitor<Path> {
    private Path source;
    private Path target;
    MvFileVisitor(Path source, Path target) {
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
        Files.move(file, target.resolve(source.relativize(file)));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}

