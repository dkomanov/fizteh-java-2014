package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CommandCp extends Command {
    public CommandCp() {
        name = "cp";
        numberOfArguments = 4;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments
                & arguments.length != numberOfArguments - 1) {
            System.out.println("wrong number of arguments");
            return false;
        }
        boolean recursive =
                (arguments.length == numberOfArguments & arguments[1].equals("-r"));
        if (!recursive) {
            return generalCopy(arguments);
        } else {
            return recursiveCopy(arguments);
        }
    }

    private boolean generalCopy(final String[] arguments) {
        if (numberOfArguments - 1 != arguments.length) {
            System.out.println("wrong number of arguments");
            return  false;
        }
        Path startPath = PathsFunction.toAbsolutePathString(arguments[1]);
        Path destinationPath = PathsFunction.toAbsolutePathString(arguments[2]);
        Path fileName = Paths.get(arguments[1]).getFileName();

        if (startPath.equals(destinationPath)) {
            System.out.println(name + ": \'" + arguments[1] + "\' and \'"
                               + arguments[2] + "\' are the same file or directory");
            return false;
        }
        if (Files.isDirectory(startPath)) {
            System.out.println(name + ": " + arguments[1]
                               + " is a directory (not copied).");
            return false;
        }

        if (!Files.exists(startPath)) {
            System.out.println(name + ": cannot copy \'" + arguments[1] + "\'"
                               + ": No such file or directory");
            return false;
        }

        if (Files.isDirectory(destinationPath)) {
            destinationPath = destinationPath.resolve(fileName);
        } else if (destinationPath.toString().endsWith("/")) {
            System.out.println(name + ": cannot copy \'" + fileName + "\' to \'"
                               + arguments[2] + "\': Not a directory");
            return false;
        }

        try {
            Files.copy(startPath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println(name + "; cannot create regular file \'" + arguments[2]
                               + "\'" + ": Not a directory");
            return false;
        }
        return true;
    }

    private boolean recursiveCopy(final String[] arguments) {
        Path startPath = PathsFunction.toAbsolutePathString(arguments[2]);

        Path destinationPath = PathsFunction.toAbsolutePathString(arguments[3]);


        if (!Files.isDirectory(destinationPath) & Files.exists(destinationPath)) {
            System.out.println("\'" + arguments[3] + "\' isn't a directory");
            return false;
        }
        if (startPath.equals(destinationPath)) {
            System.out.println(name + ": \'" + arguments[2] + "\' and \'"
                    + arguments[3] + "\' are the same file or directory");
            return false;
        }

        if (Files.exists(destinationPath)) {
            destinationPath =
                    destinationPath.resolve(startPath.getFileName()).normalize();
        }

        FileVisitorCopy myFileVisitorCopy =
                new FileVisitorCopy(startPath, destinationPath);
        try {
            Files.walkFileTree(startPath, myFileVisitorCopy);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
