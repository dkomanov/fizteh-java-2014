package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.*;

public class FileSystem {
    public void setWorkingDirectory(String directory) {
        Path newWorkingDirectory = getPath(directory);

        if (!newWorkingDirectory.toFile().exists()) {
            throw new IllegalArgumentException(String.format("'%s': No such file or directory", directory));
        }

        workingDirectory = newWorkingDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory.toString();
    }

    public String[] listWorkingDirectory() {
        File[] files = workingDirectory.toFile().listFiles();
        String[] result = new String[files.length];

        for (int i = 0; i < files.length; ++i) {
            result[i] = files[i].getName();
        }

        return result;
    }

    public ArrayList<String> getFileLines(String fileName) throws IOException {
        File file = getPath(fileName).toFile();
        ArrayList<String> lines = new ArrayList<String>();

        try (
                FileReader reader =
                    new FileReader(file);
                BufferedReader buffer =
                    new BufferedReader(reader)
        ) {
            String line;

            while ((line = buffer.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    public void createDirectory(String directoryName) {
        Path directoryPath = getPath(directoryName);
        File directoryFile = directoryPath.toFile();

        if (directoryFile.exists()) {
            throw new IllegalArgumentException(String.format("cannot create directory '%s': File exists",
                        directoryName));

        }

        directoryFile.mkdir();
    }

    public void remove(String fileName, boolean recursive) {
        File file = getPath(fileName).toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("cannot storageRemove '%s': No such file or directory",
                        fileName));
        }

        if (file.isFile()) {
            file.delete();
        } else {
            if (!recursive) {
                throw new IllegalArgumentException(String.format("cannot remove '%s' without -r flag", fileName));
            }

            recursiveRemove(file);
        }
    }

    public void moveFiles(String source, String destination) throws IOException {
        Path sourcePath = getPath(source);
        Path destinationPath = getPath(destination);
        File sourceFile = sourcePath.toFile();
        File destinationFile = destinationPath.toFile();

        if (!sourceFile.exists()) {
            throw new IllegalArgumentException(String.format("'%s' does not exist", source));
        }

        if (destinationFile.isDirectory()) {
            copyFiles(source, destination, true);
            remove(source, true);
        } else {
            String sourceDir = sourceFile.getParentFile().getCanonicalPath();
            String destinationDir = destinationFile.getParentFile().getCanonicalPath();

            if (!sourceFile.isFile()) {
                throw new IllegalArgumentException("cannot rename a file to a directory");
            }

            Files.move(sourcePath, destinationPath, REPLACE_EXISTING);
        }
    }

    public void copyFiles(String source, String destination, boolean recursive) throws IOException {
        final Path sourcePath = getPath(source);
        final Path sourceParentPath = sourcePath.getParent();
        final Path destinationPath = getPath(destination);

        if (!Files.exists(sourcePath)) {
            throw new IllegalArgumentException(String.format("cannot copy '%s': No such file or directory", source));
        }

        if (Files.exists(destinationPath) && Files.isSameFile(sourcePath, destinationPath)) {
            throw new IllegalArgumentException(String.format("'%s' and '%s' are the same file", source, destination));
        }

        if (Files.isDirectory(sourcePath) && !recursive) {
            throw new IllegalArgumentException(String.format("cannot copy '%s' without -r flag", source));
        }

        if (Files.isRegularFile(destinationPath)) {
            if (Files.isRegularFile(sourcePath)) {
                Files.copy(sourcePath, destinationPath);
                return;
            }

            if (Files.isDirectory(sourcePath)) {
                throw new IllegalArgumentException(String.format("cannot overwrite non-directory"
                            + "'%s' with directory '%s'",
                            destination, source));
            }
        }

        if (Files.isRegularFile(sourcePath) && !Files.exists(destinationPath)) {
            Files.copy(sourcePath, destinationPath);
            return;
        }

        if (!Files.exists(destinationPath)) {
            Files.createDirectory(destinationPath);
        }

        Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceParentPath.relativize(dir);
                Path destinationDir = destinationPath.resolve(relative);

                if (!destinationDir.equals(destinationPath) || !Files.exists(destinationPath)) {
                    Files.createDirectory(destinationDir);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceParentPath.relativize(file);
                Path destinationFile = destinationPath.resolve(relative);

                if (Files.exists(destinationFile)) {
                    throw new IllegalArgumentException(String.format("'%s': file already exists",
                            destinationFile.toString()));
                }

                Files.copy(file, destinationFile);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void recursiveRemove(File directory) {
        File[] files = directory.listFiles();

        for (final File file : files) {
            if (file.isDirectory()) {
                recursiveRemove(file);
            }

            file.delete();
        }

        directory.delete();
    }

    private Path getPath(String fileName) {
        Path filePath = Paths.get(fileName);

        if (workingDirectory != null && !filePath.isAbsolute()) {
            filePath = workingDirectory.resolve(filePath);
        }

        filePath = filePath.toAbsolutePath().normalize();

        return filePath;
    }

    public FileSystem() {
        File file = new File(".");

        setWorkingDirectory(file.getAbsolutePath());
    }

    private Path workingDirectory;
}
