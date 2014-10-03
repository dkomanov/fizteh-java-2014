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
        if ("-r".equals(arguments.get(0))) {
            isRecursive = true;
            firstFileIndex++;
        }

        File sourceFile = getResolvedFile(arguments.get(firstFileIndex), true);
        File targetFile = getResolvedFile(arguments.get(firstFileIndex + 1), false);


        if (sourceFile.isDirectory() && !isRecursive) {
            throw new Exception(sourceFile.getName() + "is a directory (not copied)");
        }

        if (targetFile.exists()) {
            if (targetFile.isDirectory()) {
                try {
                    targetFile = Paths.get(targetFile.getAbsolutePath(), sourceFile.getName()).toFile();
                } catch (InvalidPathException ipe) {
                    throw new Exception("target path is broken");
                }
            }
        }

        if (!isRecursive) {
            try {
                sourceFile = sourceFile.getCanonicalFile();
                targetFile = targetFile.getCanonicalFile();
                if (sourceFile.equals(targetFile)) {
                    throw new Exception("copy to the same file");
                }
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (SecurityException se) {
                throw new Exception("permission denied");
            } catch (IOException ioe) {
                throw new Exception("i/o error");
            }
        } else {
            final Path sourcePath = sourceFile.toPath();
            final Path targetPath = targetFile.toPath();
            checkForSubdirectory(sourceFile, targetFile);
            Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                throws IOException {
                            Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
                            try {
                                Files.copy(dir, targetDir, StandardCopyOption.REPLACE_EXISTING);
                            } catch (SecurityException se) {
                                throw new IOException("permission denied");
                            } catch (IOException ioe) {
                                throw new IOException("i/o error");
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            try {
                                Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
                            } catch (SecurityException se) {
                                throw new IOException("permission denied");
                            } catch (IOException ioe) {
                                throw new IOException("i/o error");
                            }
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
            throw new IllegalArgumentException("usage [-r] <source> <destination>");
        }
        if (arguments.size() == 2 && "-r".equals(arguments.get(0))) {
            throw new IllegalArgumentException("usage [-r] <source> <destination>");
        }
        if (arguments.size() == 3 && !"-r".equals(arguments.get(0))) {
            throw new IllegalArgumentException("usage [-r] <source> <destination>");
        }
        if (arguments.size() >= 4) {
            throw new IllegalArgumentException("usage [-r] <source> <destination>");
        }
    }

    private void checkForSubdirectory(File sourceFile, File targetFile) throws Exception {
        try {
            sourceFile = sourceFile.getCanonicalFile();
            targetFile = targetFile.getCanonicalFile();
            File tempTargetFile = targetFile;

            while (tempTargetFile != null) {
                if (sourceFile.equals(tempTargetFile)) {
                    throw new Exception("links error");
                }
                tempTargetFile = tempTargetFile.getParentFile();
            }
        } catch (IOException ioe) {
            throw new Exception("i/o error");
        }
    }


}
