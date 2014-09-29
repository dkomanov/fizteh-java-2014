package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public class Mkdir {
    private Mkdir() {
    }

    public static void run(final String[] args, int j) throws IOException {
        if (j + 1 == 1) {
            throw new IOException(args[0] + ": missing operand");
        } else {
            if (j + 1 > 2) {
                throw new IOException(args[0] + ": too much arguments");
            } else {
                File file = Utils.absoluteFileCreate(args[1]);
                if (!file.exists()) {
                    try {
                        file.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IOException(args[0] + ": '" + args[1] + "' already exists");
                }
            }
        }
    }
}
