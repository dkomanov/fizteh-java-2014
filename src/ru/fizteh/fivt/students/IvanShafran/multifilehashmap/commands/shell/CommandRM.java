package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class CommandRM extends Command {

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing operand");
        }

        if (args.get(0).equals("-r")) {
            if (args.size() == 1) {
                throw new Exception("missing operand");
            }
        }
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        boolean recursiveRemove = false;
        String filePath;
        if (args.get(0).equals("-r")) {
            recursiveRemove = true;
            filePath = args.get(1);
        } else {
            filePath = args.get(0);
        }

        File file;
        file = new File(filePath);

        if (file.isDirectory() && !recursiveRemove) {
            throw new Exception(filePath + ": Is a directory");
        }

        try {
            deleteFile(file, recursiveRemove);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void deleteFile(File file, boolean recursiveRemove) throws Exception {
        try {
            if (!recursiveRemove) {
                Files.delete(file.toPath());
            } else {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path directory, IOException e) throws IOException {
                        if (e == null) {
                            Files.delete(directory);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new Exception("All done wrong");
        }
    }

}
