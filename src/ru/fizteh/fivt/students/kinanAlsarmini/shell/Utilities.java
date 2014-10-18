package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class Utilities {
    public static Path getAbsolutePath(Path p) {
        return p.toAbsolutePath().normalize();
    }

    public static Path joinPaths(Path a, Path b) {
        return getAbsolutePath(a.resolve(b));
    }

    public static File getAbsoluteFile(String file, Path cwd) {
        File f = new File(file);

        if (f.isAbsolute()) {
            return f;
        }

        return new File(new File(cwd.toString()), file);
    }

    public static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;

        TreeCopier(Path source, Path target) {
            this.source = source;
            this.target = target;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            CopyOption[] options = new CopyOption[0];

            Path newdir = target.resolve(source.getParent().relativize(dir));
            try {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n, skipping the subtree.", newdir, x);
                return SKIP_SUBTREE;
            }

            return CONTINUE;
        }

        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            try {
                Files.copy(file, target.resolve(source.getParent().relativize(file)));
            } catch (IOException e) {
                throw new IllegalArgumentException("I/O error while copying file: " + file.toString() + ".");
            }

            return CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            if (exc == null) {
                Path newdir = target.resolve(source.getParent().relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x) {
                    System.err.println(x.getMessage());
                }
            }

            return CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                throw new IllegalArgumentException("Cycle detected while copying file: " + file.toString() + ".");
            }

            System.err.println(exc);

            return CONTINUE;
        }
    }
}
