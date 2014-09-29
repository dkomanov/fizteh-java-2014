package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public final class Ls {
    private Ls() {
    }

    public static void run(final String[] args, int j) throws IOException {
        if (j + 1 > 1) {
            throw new IOException(args[0] + ": too many arguments");
        }
        File file = new File(CurrentDir.getCurrentDirectory());
        for (String element : file.list()) {
            System.out.print(element + " ");
        }
        System.out.print("\n");
    }
}
