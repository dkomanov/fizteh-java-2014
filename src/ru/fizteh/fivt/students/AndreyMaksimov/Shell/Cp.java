package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import static java.nio.file.StandardCopyOption.*;

public class Cp extends Instruction {
    public Cp() {
        nameOfInstruction = "cp";
    }

    public String recursiveFlag = "-r";

    public void copyFile(final Path sourcePath, final Path destinationPath, final boolean isRecursive)
            throws IOException {
        if (!Files.isDirectory(sourcePath)) {
            Files.copy(sourcePath, destinationPath, REPLACE_EXISTING);
        } else if (isRecursive) {
            Files.copy(sourcePath, destinationPath, REPLACE_EXISTING);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
                for (Path entryPath : directoryStream) {
                    copyFile(entryPath, destinationPath.resolve(entryPath.getFileName()).normalize(), isRecursive);
                }
            }
        } else {
            throw new DirectoryNotEmptyException(sourcePath.getFileName() + " is a directory (not copied).");
        }
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {

        if (arguments.length == 1) {
            System.out.println("ERROR: Missing source and destination operand");
            System.exit(1);
        }
        if (arguments.length == 2) {
            System.out.println("ERROR: Missing destination operand");
            System.exit(1);
        }

        boolean isRecursive = (arguments.length >= 4) && (arguments[1].equals(recursiveFlag));

        String sourceFileName;
        String destinationFileName;

        if (isRecursive) {
            sourceFileName = arguments[2];
            destinationFileName = arguments[3];
        } else {
            sourceFileName = arguments[1];
            destinationFileName = arguments[2];
        }
        Path sourcePath = presentDirectory.resolve(sourceFileName).normalize();
        Path destinationPath =
                presentDirectory.resolve(destinationFileName).resolve(sourcePath.getFileName()).normalize();

        if (Files.exists(sourcePath)) {
            try {
                copyFile(sourcePath, destinationPath, isRecursive);
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.print("ERROR: No such file or directory");
            System.exit(1);
        }
        return true;
    }
}



