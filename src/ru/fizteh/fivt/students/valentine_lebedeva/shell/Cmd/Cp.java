package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

public final class Cp {
    public static void cpRec(final String arg1, final String arg2)
            throws IOException {
        File f =
            ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(arg1);
        File f2 =
            ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(arg2);
        f2 = new File(f2.getPath(), f.getName());
        if (!f2.exists()) {
            if (!f2.mkdir()) {
                throw new IOException("Error of creating");
            }
        } else {
            throw new FileAlreadyExistsException(
                    "Directory already exists");
        }
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                cpRec(files[i].getAbsolutePath(),
                        f2.getAbsolutePath());
            } else {
                cpNorm(files[i].getAbsolutePath(),
                        f2.getAbsolutePath());
            }
        }
    }

    public static void cpNorm(
            final String arg1, final String arg2)
            throws IOException {
        File f =
            ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(arg1);
        File tmp =
            ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(arg2);
        tmp = new File(tmp.getPath(), f.getName());
        if (!tmp.exists()) {
            if (!tmp.createNewFile()) {
                throw new IOException("Error of creating");
            }
        } else {
            throw new FileAlreadyExistsException(
                    "File already exists");
        }
        FileInputStream source = new FileInputStream(f);
        FileOutputStream dest = new FileOutputStream(tmp);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = source.read(buffer)) > 0) {
            dest.write(buffer, 0, length);
        }
        source.close();
        dest.close();
    }

    public static void execute(final String[] args)
            throws IOException {
        if (args.length != 3 && args.length != 4) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        if (args.length == 4 && !args[1].equals("-r")) {
            throw new IllegalArgumentException(
                "Incorrect arguments");
        }
        int i = ru.fizteh.fivt.students.valentine_lebedeva
                    .shell.Parser.step(args[1]);
        if (args[i + 1].length() > args[i].length()
                && args[i].regionMatches(
                        0, args[i + 1], 0,
                        args[i].length())) {
            throw new IllegalArgumentException(
                "Copying is impossible");
        }
        File f1 =
            ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(args[i]);
        if (!f1.isDirectory() && !f1.isFile()) {
            throw new NoSuchFileException(
                    "No such file or directory");
        }
        if (args[1].equals("-r")) {
            cpRec(args[i], args[i + 1]);
        } else {
            if (f1.isDirectory()
                    && f1.list().length != 0) {
                throw new DirectoryNotEmptyException(
                        "Directory is not empty."
                        + " Use -r flag");
            }
            cpNorm(args[i], args[i + 1]);
        }
    }

    private Cp() {
    }
}
