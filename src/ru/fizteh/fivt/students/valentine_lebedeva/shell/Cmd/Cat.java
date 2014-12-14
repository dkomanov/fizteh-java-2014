package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public final class Cat {
    public static void execute(final String[] args)
            throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        File f1 = ru.fizteh.fivt.students.valentine_lebedeva
                .shell.Parser.getFile(args[1]);
        if (!f1.isFile()) {
            throw new FileNotFoundException(
                    "No such file or directory");
        }
        FileReader tmp = new FileReader(args[1]);
        BufferedReader f = new BufferedReader(tmp);
        String buf;
        while ((buf = f.readLine()) != null) {
            System.out.println(buf);
        }
        f.close();
    }

    private Cat() {
    }
}
