package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.*;
import java.io.IOException;


public class Rm extends Instruction {
    public Rm() {
        nameOfInstruction = "rm";
    }

    public String recursiveFlag = "-r";


    public void removeFile(Path filePath, boolean isRecursive) throws IOException {
        if (!Files.isDirectory(filePath)) {
            Files.delete(filePath);
        } else if (isRecursive) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
                for (Path newFile : directoryStream) {
                    if (Files.isDirectory(newFile)) {
                        removeFile(newFile, true);
                    } else {
                        Files.delete(newFile);
                    }
                }
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
            Files.delete(filePath);
        }
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {

        if (arguments.length == 1) {
            System.out.println("ERROR: Missing operand");
            System.exit(1);
        }

        boolean isRecursive = (arguments.length >= 3) && (arguments[1].equals(recursiveFlag));
        String fileName;
        if (isRecursive) {
            fileName = arguments[2];
        } else {
            fileName = arguments[1];
        }

        Path needPath = Paths.get(fileName);
        if (!needPath.isAbsolute()) {
            needPath = Paths.get(presentDirectory.toString(), fileName).toAbsolutePath().normalize();
        }
        if (Files.exists(needPath)) {
            try {
                removeFile(needPath, isRecursive);
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
        return true;
    }
}
