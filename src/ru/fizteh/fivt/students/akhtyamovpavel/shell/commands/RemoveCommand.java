package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class RemoveCommand extends FileCommand {
    public RemoveCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        checkArgumentNumberCorrection(arguments);
        int fileIndex = 0;
        boolean isRecursive = false;
        if (arguments.get(0).equals("-r")) {
            isRecursive = true;
            fileIndex++;
        }

        File sourceFile = getResolvedFile(arguments.get(fileIndex));

        if (sourceFile.isDirectory() && !isRecursive) {
            throw new Exception("rm: " + sourceFile.getName() + ": is a directory");
        }

        if (!isRecursive) {
            try {
                Files.delete(sourceFile.toPath());
            } catch (SecurityException se) {
                throw new Exception("rm: permission denied");
            } catch (IOException ioe) {
                throw new Exception("rm: permission denied");
            }
        } else {
            Path sourcePath = sourceFile.toPath();
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw e;
                    }
                }
            });
        }
    }


    @Override
    public String getName() {
        return "rm";
    }

    @Override
    protected void checkArgumentNumberCorrection(ArrayList<String> arguments) {
        if (arguments.isEmpty() || arguments.size() >= 3) {
            throw new IllegalArgumentException("rm: usage <file/dir> [-r]");
        }
        if (arguments.size() == 1 && arguments.get(0).equals("-r")) {
            throw new IllegalArgumentException("rm: usage <file/dir> [-r]");
        }
        if (arguments.size() == 2 && !arguments.get(0).equals("-r")) {
            throw new IllegalArgumentException("rm: usage <file/dir> [-r]");
        }
    }
}

