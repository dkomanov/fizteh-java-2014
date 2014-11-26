package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.*;

/**
 * "mv" command.
 * Created by sautin1 on 9/30/14.
 */
public class MvCommand extends AbstractShellCommand {

    public MvCommand() {
        super("mv", 2, 2);
    }

    private boolean isEmptyDir(final Path dirPath) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
            return !dirStream.iterator().hasNext();
        }
    }

    /**
     * Checks whether sourcePath file can be moved to destPath.
     * @param sourcePath - source file/directory path.
     * @param destPath - destination file/directory path.
     * @return null, if file can be copied; String with error text, otherwise.
     */
    private String checkMovePaths(final Path sourcePath, final Path destPath) {
        String message;
        if (!Files.exists(sourcePath)) {
            return "cannot stat \'" + sourcePath.getFileName() + "\': No such file or directory";
        }
        if (sourcePath.equals(destPath)) {
            return sourcePath.getFileName() + "\' and \'" + destPath.getFileName() + "\' are the same file";
        }
        if (Files.isDirectory(sourcePath) && Files.exists(destPath) && !Files.isDirectory(destPath)) {
            message = "cannot overwrite non-directory \'" + destPath.getFileName();
            message += "\' with directory \'" + sourcePath.getFileName() + "\'";
            return message;
        }
        return null;
    }

    /**
     * Move (rename) files and directories.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, final String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        String sourceFileName = args[1];
        String destFileName = args[2];

        Path sourcePath = state.getPWD().resolve(sourceFileName).normalize();
        Path destPath = state.getPWD().resolve(destFileName).normalize();

        String checkMessage = checkMovePaths(sourcePath, destPath);
        if (checkMessage != null) {
            throw new CommandExecuteException(toString() + ": " + checkMessage);
        } else if (!Files.exists(destPath) && destFileName.endsWith("/") && !Files.isDirectory(sourcePath)) {
            checkMessage = "cannot create regular file \'" + destFileName + "\': Not a directory";
            throw new CommandExecuteException(toString() + ": " + checkMessage);
        }

        if (Files.isDirectory(destPath)) {
            destPath = destPath.resolve(sourcePath.getFileName()).normalize();
        }
        try {
            Files.move(sourcePath, destPath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CommandExecuteException(toString() + ": " + e.getMessage());
        }
    }

}
