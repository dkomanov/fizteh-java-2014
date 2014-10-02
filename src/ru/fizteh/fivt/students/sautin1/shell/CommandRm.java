package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandRm extends Command {
    public String recursiveFlag = "-r";


    public CommandRm() {
        minArgNumber = 1;
        commandName = "rm";
    }

    public void removeFile(Path filePath, boolean isRecursive) throws IOException {
        if (!Files.isDirectory(filePath)) {
            Files.delete(filePath);
        } else if (isRecursive) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
                for (Path entryPath : directoryStream) {
                    removeFile(entryPath, isRecursive);
                }
            }
            Files.delete(filePath);
        } else {
            throw new DirectoryNotEmptyException(filePath.getFileName() + ": is a directory");
        }
    }

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

        Path fileAbsolutePath = Paths.get(fileName);
        if (!fileAbsolutePath.isAbsolute()) {
            fileAbsolutePath = Paths.get(presentWorkingDirectory.toString(), fileName).toAbsolutePath().normalize();
        }
        if (Files.exists(fileAbsolutePath)) {
            try {
                removeFile(fileAbsolutePath, isRecursive);
            } catch (IOException e) {
                throw new IOException(toString() + ": " + e.getMessage());
            }
        } else {
            throw new NoSuchFileException(toString() + ": cannot remove \'" + fileName + "\': No such file or directory");
        }
    }

}
