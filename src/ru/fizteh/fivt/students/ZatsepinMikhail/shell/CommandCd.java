package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Files;
import java.nio.file.Path;

public class CommandCd extends Command {
    public CommandCd() {
        name = "cd";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length == 1) {
            System.setProperty("user.dir", "/");
            return true;
        }
        Path newWorkDirectory = PathsFunction.toAbsolutePathString(arguments[1]);
        if (Files.isDirectory(newWorkDirectory)) {
            System.setProperty("user.dir", newWorkDirectory.toString());
            return true;
        } else {
            if (Files.exists(newWorkDirectory)) {
                System.out.println(name + ": \'" + arguments[1]
                                   + "\': Not a directory");
            } else {
                System.out.println(name + ": \'" + arguments[1]
                                   + "\': No such file or directory");
            }
            return false;
        }
    }
}
