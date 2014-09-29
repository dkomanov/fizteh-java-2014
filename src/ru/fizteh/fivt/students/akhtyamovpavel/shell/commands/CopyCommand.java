package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */

public class CopyCommand extends FileCommand {


    public CopyCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        checkArgumentNumberCorrection(arguments);
        boolean isRecursive = false;
        int firstFileIndex = 0;
        if (arguments.get(0).equals("-r")) {
            isRecursive = true;
            firstFileIndex++;
        }

        File sourceFile = getResolvedFile(arguments.get(firstFileIndex));
        File targetFile = getResolvedFile(arguments.get(firstFileIndex + 1));
        if (sourceFile.isDirectory() && !isRecursive) {
            throw new Exception("cp: " + sourceFile.getName() + "is a directory (not copied)");
        }
        if (!targetFile.isDirectory()) {
            throw new Exception("cp: " + targetFile.getName() + "not a directory: can't copy");
        }
        targetFile = Paths.get(targetFile.getAbsolutePath(), sourceFile.getName()).toFile();

        if (!isRecursive) {
            try {
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (SecurityException se) {
                throw new Exception("cp: permission denied");
            } catch (IOException ioe) {
                throw new Exception("cp: i/o error");
            }
        } else {
            Path sourcePath = sourceFile.toPath();
            Path targetPath = targetFile.toPath();
            Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
                        try {
                            Files.copy(dir, targetDir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetDir)) {
                                throw e;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                }
            );
        }
    }

    @Override
    public String getName() {
        return "cp";
    }

    @Override
    protected void checkArgumentNumberCorrection(ArrayList<String> arguments) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("cp: isn't enough arguments");
        }
        if (arguments.size() == 2 && arguments.get(0).equals("-r")) {
            throw new IllegalArgumentException("cp: isn't enough arguments");
        }
        if (arguments.size() == 3 && !arguments.get(0).equals("-r")) {
            throw new IllegalArgumentException("cp: too many arguments");
        }
        if (arguments.size() >= 4) {
            throw new IllegalArgumentException("cp: too many arguments");
        }
    }


}
