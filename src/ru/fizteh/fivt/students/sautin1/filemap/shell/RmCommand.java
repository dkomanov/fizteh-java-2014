package ru.fizteh.fivt.students.sautin1.filemap.shell;

import java.io.IOException;
import java.nio.file.*;

/**
 * "rm" command.
 * Created by sautin1 on 9/30/14.
 */
public class RmCommand extends AbstractShellCommand {
    public final String recursiveFlag = "-r";


    public RmCommand() {
        super("rm", 1, 2);
    }

    /**
     * Removes file/directory from filePath. If "-r" flag is true, removes all files/subdirectories
     * in directory sourcePath.
     * @param filePath - path to the file to be removed.
     * @param isRecursive - flag whether the contents of the directory, specified by filePath, have to be removed.
     * @throws java.io.IOException if any IO error occurs.
     */
     public void removeFile(Path filePath, boolean isRecursive) throws IOException {
        if (!Files.isDirectory(filePath)) {
            Files.delete(filePath);
        } else if (isRecursive) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
                for (Path entryPath : directoryStream) {
                    removeFile(entryPath, true);
                }
            }
            Files.delete(filePath);
        } else {
            throw new DirectoryNotEmptyException(filePath.getFileName() + ": is a directory");
        }
    }

    /**
     * Removes file/directory from filePath using removeFile().
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        boolean isRecursive = (args.length - 1 == getMaxArgNumber()) && (args[1].equals(recursiveFlag));
        String fileName;
        if (isRecursive) {
            fileName = args[2];
        } else {
            fileName = args[1];
        }

        Path fileAbsolutePath = state.getPWD().resolve(fileName).normalize();
        try {
            removeFile(fileAbsolutePath, isRecursive);
        } catch (IOException e) {
            String exceptionMessage = toString() + ": ";
            if (e instanceof NoSuchFileException) {
                exceptionMessage += "cannot remove \'" + fileName + "\': No such file or directory";
            } else {
                exceptionMessage += e.getMessage();
            }
            throw new CommandExecuteException(exceptionMessage);
        }
    }

}
