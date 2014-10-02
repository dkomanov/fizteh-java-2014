package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.*;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandMv extends Command {

    public CommandMv() {
        minArgNumber = 2;
        commandName = "mv";
    }

    private boolean isEmptyDir(final Path dirPath) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
            return !dirStream.iterator().hasNext();
        }
    }

    @Override
    public void execute(final String... args) throws IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        String sourceFileName;
        String destFileName;
        sourceFileName = args[1];
        destFileName = args[2];

        Path sourcePath = presentWorkingDirectory.resolve(sourceFileName).normalize();
        Path destPath = presentWorkingDirectory.resolve(destFileName).normalize();

        if (Files.exists(sourcePath)) {
            if (Files.isDirectory(destPath)) {
                destPath = destPath.resolve(sourcePath.getFileName()).normalize();
                if (Files.exists(destPath) && Files.isDirectory(destPath) && !isEmptyDir(destPath)) {
                    throw new IOException(toString() + ": cannot move \'" + sourcePath.getFileName() + "\' to \'" + destPath.getFileName() + "\': Directory not empty");
                }
            }
            try {
                Files.move(sourcePath, destPath, REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException(toString() + ": " + e.getMessage());
            }
        } else {
            throw new NoSuchFileException(toString() + ": cannot stat \'" + sourcePath.getFileName() + "\': No such file or directory");
        }
    }

}
