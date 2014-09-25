package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

//работает
public class Mkdir {
    public Mkdir(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length > 2) {
            System.err.println("extra arguments for mkdir");
            System.exit(1);
        }
        if (currentArgs.length > 2) {
            System.err.println("I need name for directory");
            System.exit(1);

        }
        File f = new File(cd.getCurrentDirectory(), currentArgs[1]);
        if (!f.mkdirs()) {
            System.err.println("I can't create the directory");
            System.exit(1);
        }
    }
}
