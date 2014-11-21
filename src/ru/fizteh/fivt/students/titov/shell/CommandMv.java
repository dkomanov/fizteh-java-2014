package ru.fizteh.fivt.students.titov.shell;

import java.nio.file.Files;
import java.nio.file.Path;

public class CommandMv extends Command {
    public CommandMv() {
        name = "mv";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments) {
            System.out.println("wrong number of arguments");
            return false;
        }
        Path startPath = PathsFunction.toAbsolutePathString(arguments[1]);
        Path destinationPath = PathsFunction.toAbsolutePathString(arguments[2]);
        Path fileName = startPath.getFileName();

        if (!Files.exists(startPath)) {
            System.out.println(name + ": cannot stat \'" + arguments[1]
                               + "\': No such file or directory");
            return false;
        }

        if (Files.isDirectory(destinationPath)) {
            destinationPath = destinationPath.resolve(fileName);
        } else if (destinationPath.toString().endsWith("/")) {
            System.out.println(name + ": cannot move \'" + arguments[1] + "\' to \'"
                               + arguments[2] + "\': Not a directory");
            return false;
        }

        try {
            Files.move(startPath, destinationPath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
