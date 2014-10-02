package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import static java.nio.file.StandardCopyOption.*;

public class Mv extends Instruction {
    public Mv() {
        nameOfInstruction = "mv";
    }

    public void copyFile(final Path sourcePath, final Path destinationPath, final boolean isRecursive)
            throws IOException {
        if (!Files.isDirectory(sourcePath)) {
            Files.copy(sourcePath, destinationPath, REPLACE_EXISTING);
            Files.delete(sourcePath); //Удалили файл на данном шаге;
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

        String sourceFileName = arguments[1];
        String destinationFileName = arguments[2];

        Path sourcePath = presentDirectory.resolve(sourceFileName).normalize();
        Path destinationPath
                = presentDirectory.resolve(destinationFileName).resolve(sourcePath.getFileName()).normalize();

        boolean isRecursive;

        if (Files.exists(sourcePath)) {
            if (Files.isDirectory(sourcePath)) {
                isRecursive = true;
            } else {
                isRecursive = false;
            }

            try {
                copyFile(sourcePath, destinationPath, isRecursive);
                if (isRecursive) {
                    Files.delete(sourcePath);
                }
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
        return true;
    }
}
