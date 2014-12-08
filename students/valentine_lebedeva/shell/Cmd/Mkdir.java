package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public final class Mkdir {
    public static void execute(final String[] args)
            throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        File f = new File(System.getProperty("user.dir"), args[1]);
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("Creation is failed");
            }
        } else {
            throw new FileAlreadyExistsException(
                    "Direcory already exists");
        }
    }

    private Mkdir() {
    }
}
