package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public final class Mv {
    public static void execute(final String[] args)
            throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        File f =
            ru.fizteh.fivt.students.valentine_lebedeva
            .shell.Parser.getFile(args[1]);
        if (!f.isDirectory() && !f.isFile()) {
            throw new NoSuchFileException(
                    "No such file or directory");
        }
        if (f.isDirectory()) {
            Cp.cpRec(args[1], args[2]);
            Rm.rmRec(args[1]);
        } else {
            Cp.cpNorm(args[1], args[2]);
            Rm.rmNorm(args[1]);
        }
    }

    private Mv() {
    }
}
