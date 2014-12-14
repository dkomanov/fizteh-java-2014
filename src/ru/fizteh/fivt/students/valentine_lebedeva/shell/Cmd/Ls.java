package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;

public final class Ls {

    public static void execute(final String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        File f = new File(System.getProperty("user.dir"));
        String[] files = f.list();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
    }

    private Ls() {
    }
}
