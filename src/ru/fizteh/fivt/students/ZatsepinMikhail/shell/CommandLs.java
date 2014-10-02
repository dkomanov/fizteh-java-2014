package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.File;

public class CommandLs extends Command {
    public CommandLs() {
        name = "ls";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments) {
            return false;
        }
        String[] listOfFiles = new File(System.getProperty("user.dir")).list();
        for (String oneFileName : listOfFiles) {
            System.out.println(oneFileName);
        }
        return true;
    }
}
