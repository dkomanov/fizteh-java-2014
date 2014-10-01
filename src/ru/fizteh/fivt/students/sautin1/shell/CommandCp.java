package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandCp extends Command {
    public String recursiveFlag = "-r";

    public CommandCp() {
        minArgNumber = 2;
        commandName = "cp";
    }

    public void copyFile(Path sourceFilePath, Path destFilePath, boolean isRecursive) throws IOException {
        if (!Files.isDirectory(sourceFilePath)) {
            Files.copy(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
        } else if (isRecursive) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceFilePath)) {
                for (Path newFilePath : directoryStream) {
                    if (Files.isDirectory(newFilePath)) {
                        Files.copy(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                        Path newDestPath = Paths.get(destFilePath.toString(), newFilePath.toString()).toAbsolutePath().normalize();
                        copyFile(newFilePath, newDestPath, true);
                    } else {
                        Files.copy(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } else {
            throw new DirectoryNotEmptyException(sourceFilePath.getFileName() + " is a directory (not copied).");
        }
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        boolean isRecursive = (args.length >= 4) && (args[1].equals(recursiveFlag));
        String sourceFileName;
        String destFileName;
        if (isRecursive) {
            sourceFileName = args[2];
            destFileName = args[3];
        } else {
            sourceFileName = args[1];
            destFileName = args[2];
        }

        Path sourceFileAbsolutePath = Paths.get(sourceFileName);
        Path destFileAbsolutePath = Paths.get(destFileName);
        if (!sourceFileAbsolutePath.isAbsolute()) {
            sourceFileAbsolutePath = Paths.get(presentWorkingDirectory.toString(), sourceFileName).toAbsolutePath().normalize();
        }
        if (!destFileAbsolutePath.isAbsolute()) {
            destFileAbsolutePath = Paths.get(presentWorkingDirectory.toString(), destFileName).toAbsolutePath().normalize();
        }
        if (Files.exists(sourceFileAbsolutePath)) {
            if (Files.exists(destFileAbsolutePath)) {
                try {
                    copyFile(sourceFileAbsolutePath, destFileAbsolutePath, isRecursive);
                } catch (IOException e) {
                    throw new IOException(toString() + ": " + e.getMessage());
                }
            } else {
                throw new NoSuchFileException(toString() + ": cannot stat \'" + destFileAbsolutePath.getFileName() + "\': No such file or directory");
            }
        } else {
            throw new NoSuchFileException(toString() + ": cannot stat \'" + sourceFileAbsolutePath.getFileName() + "\': No such file or directory");
        }
    }

}
