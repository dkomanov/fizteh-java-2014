package ru.fizteh.fivt.students.SibgatullinDamir.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class CpCommand implements Commands {

    void copyNonrecursive(String source, String destination) throws MyException {

        Path sourcePath = Shell.currentPath.resolve(source);
        Path destinationPath = Shell.currentPath.resolve(destination);

        if (Files.isDirectory(sourcePath)) {
            throw new MyException("cp: " + source + " is a directory (not copied).");
        }
        if (!Files.exists(sourcePath)) {
            throw new MyException("cp: '" + source + "' does not exist");
        }
        if (sourcePath.equals(destinationPath)) {
            throw new MyException("cp: copying file or directory into itself");
        }

        if (!Files.exists(destinationPath)) {

            if (!Files.exists(destinationPath.getParent())) {
                throw new MyException("cp: the destination directory does not exist");
            } else {
                copyFinal(sourcePath, destinationPath);
            }

        } else {

            if (sourcePath.getParent().equals(destinationPath.getParent())) {
                if (Files.isDirectory(destinationPath)) {
                    destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
                }
                copyFinal(sourcePath, destinationPath);
            } else {
                if (Files.isDirectory(sourcePath)) {
                    destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
                }
                copyFinal(sourcePath, destinationPath);
            }

        }
    }

    void copyRecursive(String source, String destination) throws MyException {

        Path sourcePath = Shell.currentPath.resolve(source);
        Path destinationPath = Shell.currentPath.resolve(destination);

        if (!Files.exists(sourcePath)) {
            throw new MyException("cp: '" + source + "' does not exist");
        }

        if (Files.isDirectory(sourcePath)) {

            if (!Files.exists(destinationPath)) {

                if (Files.exists(destinationPath.getParent())) {
                    if (!destinationPath.getParent().equals(sourcePath.getParent())) {
                        checkRecursiveCopy(sourcePath, destinationPath);
                    }
                    copyFinal(sourcePath, destinationPath);
                } else {
                    throw new MyException("cp: the destination directory does not exist");
                }

            } else {
                if (!sourcePath.getParent().equals(destinationPath.getParent())) {
                    checkRecursiveCopy(sourcePath, destinationPath);
                }
                destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
                copyFinal(sourcePath, destinationPath);
            }

        } else {
            if (!Files.exists(destinationPath)) {

                if (Files.exists(destinationPath.getParent())) {
                    copyFinal(sourcePath, destinationPath);
                } else {
                    throw new MyException("cp: the destination directory does not exist");
                }

            } else {
                if (!sourcePath.getParent().equals(destinationPath.getParent())) {
                    if (Files.isDirectory(destinationPath)) {
                        destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
                    }
                }
                destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
                copyFinal(sourcePath, destinationPath);
            }
        }
    }

    private void checkRecursiveCopy(Path sourcePath, Path destinationPath) throws MyException {
        if (destinationPath.toString().startsWith(sourcePath.toString())) {
            throw new MyException("cp: recursive copying directory into itself");
        }

    }

    void copyFinal(Path sourcePath, Path destinationPath) throws MyException {
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MyException("cp: I/O error occurs");
        }

        if (!Files.isDirectory(sourcePath)) {
            return;
        }

        File dir;
        dir = new File(sourcePath.toString());
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file: files) {

                Path tempSourcePath = Paths.get(sourcePath.toString(), file.getName());
                Path tempDestinationPath = Paths.get(destinationPath.toString(), file.getName());

                try {
                    Files.copy(tempSourcePath, tempDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new MyException("cp: I/O error occurs");
                }

                if (file.isDirectory()) {
                    copyFinal(tempSourcePath, tempDestinationPath);
                }
            }
        }
    }

    public void execute(String[] args) throws MyException {
        if (args.length < 3) {
            throw new MyException("cp: not enough arguments");
        }
        if (args.length > 4) {
            throw new MyException("cp: too many arguments");
        }
        if (args[1].equals("-r") && args.length == 4) {
            copyRecursive(args[2], args[3]);
        } else if (args.length == 3) {
            copyNonrecursive(args[1], args[2]);
        } else { throw new MyException("cp: invalid arguments"); }
    }

    public String getName() {
        return "cp";
    }
}
