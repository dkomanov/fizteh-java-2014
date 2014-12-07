package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.fileshell;

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
    public RemoveCommand(Path path) {
        link = path;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        checkArgumentNumberCorrection(arguments);
        int fileIndex = 0;
        boolean isRecursive = false;
        if (arguments.get(0).equals("-r")) {
            isRecursive = true;
            fileIndex++;
        }

        File sourceFile = getResolvedFile(arguments.get(fileIndex), true);

        if (sourceFile.isDirectory() && !isRecursive) {
            throw new Exception(sourceFile.getName() + ": is a directory");
        }

        if (!isRecursive) {
            try {
                Files.delete(sourceFile.toPath());
            } catch (SecurityException se) {
                throw new Exception("permission denied");
            } catch (IOException ioe) {
                throw new Exception("permission denied");
            }
        } else {
            Path sourcePath = sourceFile.toPath();
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        Files.delete(file);
                    } catch (SecurityException se) {
                        throw new IOException("permission denied");
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (e == null) {
                        try {
                            Files.delete(dir);
                        } catch (SecurityException se) {
                            throw new IOException("permission denied");
                        } catch (IOException ioe) {
                            throw new IOException("i/o error");
                        }
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw new IOException("i/o error");
                    }
                }
            });
        }
        return "folder removed";
    }


    @Override
    public String getName() {
        return "rm";
    }

    @Override
    protected void checkArgumentNumberCorrection(ArrayList<String> arguments) throws Exception {
        if (arguments.isEmpty() || arguments.size() >= 3) {
            throw new Exception("usage <file/dir> [-r]");
        }
        if (arguments.size() == 1 && arguments.get(0).equals("-r")) {
            throw new Exception("usage <file/dir> [-r]");
        }
        if (arguments.size() == 2 && !arguments.get(0).equals("-r")) {
            throw new Exception("usage <file/dir> [-r]");
        }
    }
}
