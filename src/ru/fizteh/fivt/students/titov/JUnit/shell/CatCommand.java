package ru.fizteh.fivt.students.titov.JUnit.shell;

import java.nio.file.FileSystems;
import java.nio.file.Files;

public class CatCommand extends Command {
    public CatCommand() {
        name = "cat";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments) {
            System.err.println("wrong number of arguments");
            return false;
        }
        try {
            Files.copy(FileSystems.getDefault().getPath(arguments[1]), System.err);
        } catch (Exception e) {
            System.err.println(name + ": " + arguments[1] + ": No such file in directory");
            return false;
        }
        return true;
    }
}
