package ru.fizteh.fivt.students.titov.JUnit.shell;

import java.io.File;

public class LsCommand extends Command {
    public LsCommand() {
        name = "ls";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments) {
            System.err.println("wrong number of arguments");
            return false;
        }
        String[] listOfFiles = new File(System.getProperty("user.dir")).list();
        for (String oneFileName : listOfFiles) {
            System.err.println(oneFileName);
        }
        return true;
    }
}
