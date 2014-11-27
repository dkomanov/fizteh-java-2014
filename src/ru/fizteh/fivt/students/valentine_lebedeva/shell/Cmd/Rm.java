package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;

public final class Rm {
    public static void rmRec(final String arg)
            throws IOException {
        File f = ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Parser.getFile(arg);
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                rmRec(files[i].getPath());
            } else {
                rmNorm(files[i].getPath());
            }
        }
        rmNorm(f.getPath());
    }

    public static void rmNorm(final String arg)
            throws IOException {
        File f = ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Parser.getFile(arg);
        if (!f.exists()) {
            throw new NoSuchFileException(
                    "No such file or directory");
        }
        if (!f.delete()) {
            throw new IOException("Incorrect delete");
        }
    }

    public static void execute(final String[] args) throws
            IOException {
        if (args.length != 2
                && args.length != 3) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        if (args.length == 3
                && !args[1].equals("-r")) {
            throw new IllegalArgumentException(
                    "Incorrect arguments");
        }
        int i = ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Parser.step(args[1]);
        File f1 = ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Parser.getFile(args[i]);
        if (!f1.isDirectory() && !f1.isFile()) {
            throw new NoSuchFileException(
                        "No such file or directory");
        }
        if (args[1].equals("-r")) {
            rmRec(args[i]);
        } else {
            if (f1.isDirectory() && f1.list().length != 0) {
                throw new DirectoryNotEmptyException(
                        f1.getName()
                        + "is Directory");
            }
            rmNorm(args[i]);
        }
    }

    private Rm() {
    }
}
