package ru.fizteh.fivt.students.titov.JUnit.ShellPackage;

import java.nio.file.FileSystems;
import java.nio.file.Files;

public class CommandCat extends Command {
    public CommandCat() {
        name = "cat";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments) {
            System.out.println("wrong number of arguments");
            return false;
        }
        try {
            Files.copy(FileSystems.getDefault().getPath(arguments[1]), System.out);
        } catch (Exception e) {
            System.out.println(name + ": " + arguments[1] + ": No such file in directory");
            return false;
        }
        return true;
    }
}
