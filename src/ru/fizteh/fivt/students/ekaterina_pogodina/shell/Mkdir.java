package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public class Mkdir {
    private Mkdir() {
    }

    public static void run(final String[] args) throws IOException {
        if (args.length < 2) {
            System.exit(0);
        } else {
            File file = Utils.absoluteFileCreate(args[1]);
            file.mkdir();
        }
    }
}
