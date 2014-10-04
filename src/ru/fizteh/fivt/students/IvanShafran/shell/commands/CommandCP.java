package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

public class CommandCP extends Command {
    public Shell shellLink;

    public CommandCP(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing file operand");
        }

        if (args.get(0).equals("-r")) {
            if (args.size() == 1) {
                throw new Exception("missing file operand");
            } else if (args.size() == 2) {
                throw new Exception("missing destination file operand after" + "'" + args.get(1) + "'");
            }
        } else {
            if (args.size() == 1) {
                throw new Exception("missing destination file operand after" + "'" + args.get(1) + "'");
            }
        }
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        Path sourcePath;
        Path destinationPath;
        boolean recursiveCopy;
        if (args.get(0).equals("-r")) {
            sourcePath = Paths.get(args.get(1));
            destinationPath = Paths.get(args.get(2));
            recursiveCopy = true;
        } else {
            sourcePath = Paths.get(args.get(0));
            destinationPath = Paths.get(args.get(1));
            recursiveCopy = false;
        }

        File sourceFile;
        sourceFile = new File(getAbsolutePath(shellLink.getWorkingDirectory().toString(), sourcePath.toString()));

        File destinationFile;
        destinationFile = new File(getAbsolutePath(shellLink.getWorkingDirectory().toString(),
                destinationPath.toString(), false));

        if (!recursiveCopy && sourceFile.isDirectory()) {
            throw new Exception(sourcePath + " is a directory (not copied).");
        }

        String source = sourceFile.getAbsolutePath();
        String destination = getDestination(sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
        checkDestination(source, destination);

        try {
            doCopying(source, destination, recursiveCopy);
        } catch (Exception e) {
            throw new Exception("all done wrong");
        }
    }

    private String getDestination(String source, String destination) {
        File destinationFile = new File(destination);

        if (destinationFile.isDirectory()) {
            return Paths.get(destination, Paths.get(source).getFileName().toString()).toString();
        } else {
            return destination;
        }
    }

    private void checkDestination(String source, String destination) throws Exception {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);

        while (destinationFile != null) {
            if (sourceFile.equals(destinationFile)) {
                throw new Exception("links error");
            }
            destinationFile = destinationFile.getParentFile();
        }
    }

    private void doCopying(String source, String destination, boolean recursiveCopy) throws Exception {
        final Path sourcePath = Paths.get(source);
        final Path destinationPath = Paths.get(destination);


        if (!recursiveCopy) {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                throws IOException {
                            Path destinationDir = destinationPath.resolve(sourcePath.relativize(dir));
                            try {
                                Files.copy(dir, destinationDir);
                            } catch (FileAlreadyExistsException e) {
                                if (!Files.isDirectory(destinationDir)) {
                                    throw e;
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.copy(file, destinationPath.resolve(sourcePath.relativize(file)));
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        }
    }
}
