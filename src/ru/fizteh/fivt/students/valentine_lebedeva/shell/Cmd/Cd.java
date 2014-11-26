package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.util.Properties;

public final class Cd {
    public static void execute(
            final String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        File f = ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(args[1]);
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new InvalidPathException(
                        args[1], "Incorrect path");
            }
            Properties p = System.getProperties();
            p.put("user.dir", f.getPath());
            System.setProperties(p);
        } else {
            throw new NotDirectoryException("No such directory");
        }
    }

    private Cd() {
    }
}
