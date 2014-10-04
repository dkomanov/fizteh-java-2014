package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class FilesOperations {

    private Path currentDirectory;

    public FilesOperations() {
        try {
            File file = new File(".");
            setCurrentDirectory(file.getAbsolutePath());
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    public final Path getPath(final String dirName) {
        Path newFilePath = Paths.get(dirName);
        if ((currentDirectory != null) && (!newFilePath.isAbsolute())) {
            newFilePath = currentDirectory.resolve(dirName);
        }
        newFilePath = newFilePath.normalize();
        return newFilePath;
    }

    public final void setCurrentDirectory(final String directory)
            throws IOException {
        Path newWorkingDirectory = getPath(directory);
        if (!newWorkingDirectory.toFile().exists()) {
            throw new IOException(String.format("'%s': No such file or directory", directory));
        }
        currentDirectory = newWorkingDirectory;
    }

    public final String[] getFilesList() {
        File[] files = currentDirectory.toFile().listFiles();
        String[] filesList = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            filesList[i] = files[i].getName();
        }
        return filesList;
    }

    public final void makeNewDirectory(final String newDirectoryName)
            throws IOException {
        Path newDirectoryPath = getPath(newDirectoryName);
        File newDirectoryFile = newDirectoryPath.toFile();
        if (newDirectoryFile.exists()) {
            throw new IOException("mkdir: Directory \'"
                    + newDirectoryName + "\' already exists!");
        }
        newDirectoryFile.mkdir();
    }

    public final String getCurrentDirectory() {
        return currentDirectory.toString();
    }

    public final void moveFile(final String source, final String destination)
            throws IOException {
        Path sourcePath = getPath(source);
        Path destinationPath = getPath(destination);
        if (destinationPath.toFile().isFile()) {
            throw new IllegalArgumentException(
                    "mv: Destination path is not a directory!");
        }
        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public final void printFile(final String fileName)
            throws FileNotFoundException {
        Path filePath = getPath(currentDirectory + File.separator + fileName);
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException("cat: \'" + fileName
                    + "\': No such file or directory");
        }
        Scanner scanner = new Scanner(filePath.toFile());
        String line;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            System.out.println(line);
        }
    }

    public final void removeFile(final String fileName) {
        File file = getPath(currentDirectory + File.separator
                + fileName).toFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("rm: cannot remove \'"
                    + fileName + "\': No such file or directory");
        }
        if (file.isFile()) {
            file.delete();
        } else {
            removeDirectory(file);
        }
    }

    public final void removeDirectory(final File directory) {
        File[] filesList = directory.listFiles();
        if (filesList == null) {
            return;
        }
        for (File f: filesList) {
            removeDirectory(f);
            f.delete();
        }
        directory.delete();
    }

    public final void copyFile(final String source,
                               final String destination)
            throws IOException {
        final Path sourcePath = getPath(source);
        final Path sourceParentPath = sourcePath.getParent();
        final Path destinationPath = getPath(destination);
        if (!Files.exists(sourcePath)) {
            throw new IOException(String.format(
                    "cp: cannot copy '%s': No such file or directory", source));
        }
        if (Files.exists(destinationPath)
                && Files.isSameFile(sourcePath, destinationPath)) {
            throw new IOException(String.format(
                    "cp: '%s' and '%s' are the same file",
                    source, destination));
        }
        if (Files.isRegularFile(destinationPath)) {
            if (Files.isRegularFile(sourcePath)) {
                Files.copy(sourcePath, destinationPath);
                return;
            }
            if (Files.isDirectory(sourcePath)) {
                throw new IOException(String.format(
                        "cp: cannot overwrite non-directory"
                                + "'%s' with directory '%s!'",
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
            public FileVisitResult preVisitDirectory(
                    final Path dir, final BasicFileAttributes attrs)
                    throws IOException {
                Path relative = sourceParentPath.relativize(dir);
                Path destinationDir = destinationPath.resolve(relative);
                if (!destinationDir.equals(destinationPath)
                        || !Files.exists(destinationPath)) {
                    Files.createDirectory(destinationDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(
                    final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Path relative = sourceParentPath.relativize(file);
                Path destinationFile = destinationPath.resolve(relative);
                if (Files.exists(destinationFile)) {
                    throw new IOException(
                            String.format("'%s': file already exists",
                                    destinationFile.toString()));
                }
                Files.copy(file, destinationFile);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(
                    final Path file, final IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    final Path dir, final IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public final void printDirectory() {
        File directory = currentDirectory.toFile();
        String[] content =  directory.list();
        for (String file: content) {
            System.out.println(file);
        }
    }
}
