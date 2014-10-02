package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.*;

/**
 * "mv" command.
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

    /**
     * Move (rename) files and directories.
     * @param args [0] - command name; [1] - source file name; [2] - destination file name.
     * @throws IOException
     */
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
                    String errorMessage = toString() + ": cannot move \'" + sourcePath.getFileName();
                    errorMessage = errorMessage +  "\' to \'" + destPath.getFileName() + "\': Directory not empty";
                    throw new IOException(errorMessage);
                }
            }
            try {
                Files.move(sourcePath, destPath, REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException(toString() + ": " + e.getMessage());
            }
        } else {
            String errorMessage = toString() + ": cannot stat \'" + sourcePath.getFileName();
            errorMessage = errorMessage + "\': No such file or directory";
            throw new NoSuchFileException(errorMessage);
        }
    }

}
