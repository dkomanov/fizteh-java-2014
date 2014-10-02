package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.*;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandCp extends Command {
    public String recursiveFlag = "-r";

    public CommandCp() {
        minArgNumber = 2;
        commandName = "cp";
    }

    public void copyFile(final Path sourcePath, final Path destPath, final boolean isRecursive) throws IOException {
        if (!Files.isDirectory(sourcePath)) {
            Files.copy(sourcePath, destPath, REPLACE_EXISTING);
        } else if (isRecursive) {
            Files.copy(sourcePath, destPath, REPLACE_EXISTING);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
                for (Path entryPath : directoryStream) {
                    copyFile(entryPath, destPath.resolve(entryPath.getFileName()).normalize(), isRecursive);
                }
            }
        } else {
            throw new DirectoryNotEmptyException(sourcePath.getFileName() + " is a directory (not copied).");
        }
    }

    @Override
    public void execute(final String... args) throws IOException {
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

        Path sourcePath = presentWorkingDirectory.resolve(sourceFileName).normalize();
        Path destPath = presentWorkingDirectory.resolve(destFileName).resolve(sourcePath.getFileName()).normalize();
        if (Files.exists(sourcePath)) {
                try {
                    copyFile(sourcePath, destPath, isRecursive);
                } catch (IOException e) {
                    throw new IOException(toString() + ": " + e.getMessage());
                }
        } else {
            throw new NoSuchFileException(toString() + ": cannot stat \'" + sourcePath.getFileName() + "\': No such file or directory");
        }
    }

}
