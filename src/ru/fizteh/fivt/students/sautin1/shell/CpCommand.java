package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.*;

/**
 * "cp" command.
 * Created by sautin1 on 9/30/14.
 */
public class CpCommand extends AbstractShellCommand {
    public final String recursiveFlag = "-r";

    public CpCommand() {
        super("cp", 2, 3);
    }

    /**
     * Copies file from sourcePath to destPath.
     * If (isRecursive == true), then copies also all subdirectories of the directory, specified by sourcePath.
     * @param sourcePath - source file/directory path.
     * @param destPath - destination file/directory path.
     * @param isRecursive - flag whether the contents of the source folder have to be copied.
     * @throws IOException if error in IO occurs.
     */
    public void copyFile(final Path sourcePath, final Path destPath, final boolean isRecursive) throws IOException {
        if (!Files.isDirectory(sourcePath)) {
            Files.copy(sourcePath, destPath, REPLACE_EXISTING);
        } else if (isRecursive) {
            Files.copy(sourcePath, destPath, REPLACE_EXISTING);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
                for (Path entryPath : directoryStream) {
                    copyFile(entryPath, destPath.resolve(entryPath.getFileName()).normalize(), true);
                }
            }
        } else {
            throw new DirectoryNotEmptyException(sourcePath.getFileName() + " is a directory (not copied).");
        }
    }

    /**
     * Checks whether sourcePath file can be copied to destPath.
     * @param sourcePath - source file/directory path.
     * @param destPath - destination file/directory path.
     * @return null, if file can be copied; String with error text, otherwise.
     */
    private String checkCopyPaths(final Path sourcePath, final Path destPath) {
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
     * Copies file from sourcePath to destPath using copyFile().
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, final String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        boolean isRecursive = (args.length - 1 == getMaxArgNumber()) && (args[1].equals(recursiveFlag));
        String sourceFileName;
        String destFileName;
        if (isRecursive) {
            sourceFileName = args[2];
            destFileName = args[3];
        } else {
            sourceFileName = args[1];
            destFileName = args[2];
        }

        Path sourcePath = state.getPWD().resolve(sourceFileName).normalize();
        Path destPath = state.getPWD().resolve(destFileName).normalize();

        String checkMessage = checkCopyPaths(sourcePath, destPath);
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
            copyFile(sourcePath, destPath, isRecursive);
        } catch (IOException e) {
            String exceptionMessage = toString() + ": ";
            throw new CommandExecuteException(exceptionMessage + e.getMessage());
        }

    }

}
