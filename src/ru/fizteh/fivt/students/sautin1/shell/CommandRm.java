package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;

/**
 * "rm" command.
 * Created by sautin1 on 9/30/14.
 */
public class CommandRm extends Command {
    public String recursiveFlag = "-r";


    public CommandRm() {
        minArgNumber = 1;
        commandName = "rm";
    }

    /**
     * Removes file/directory from filePath. If "-r" flag is true, removes all files/subdirectories
     * in directory sourcePath.
     * @param filePath - path to the file to be removed.
     * @param isRecursive - flag whether the contents of the directory, specified by filePath, have to be removed.
     * @throws IOException
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
     * @param args [0] - command name; [1] - (optional) flag "-r"; [2] - file name.
     * @throws RuntimeException
     * @throws IOException
     */
    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        boolean isRecursive = (args.length >= 3) && (args[1].equals(recursiveFlag));
        String fileName;
        if (isRecursive) {
            fileName = args[2];
        } else {
            fileName = args[1];
        }

        Path fileAbsolutePath = presentWorkingDirectory.resolve(fileName);
        if (Files.exists(fileAbsolutePath)) {
            try {
                removeFile(fileAbsolutePath, isRecursive);
            } catch (IOException e) {
                throw new IOException(toString() + ": " + e.getMessage());
            }
        } else {
            String errorMessage = toString() + ": cannot remove \'" + fileName + "\': No such file or directory";
            throw new NoSuchFileException(errorMessage);
        }
    }

}
