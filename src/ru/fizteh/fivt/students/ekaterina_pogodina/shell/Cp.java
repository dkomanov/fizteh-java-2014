package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;


public class Cp {
    private Cp() {  }

    public static void run(final String[] args, boolean flag, int j) throws IOException {
        if (j + 1 < 3) {
            throw new IOException(args[0] + ": missing operand");
        } else {
            if (j + 1 > 3) {
                throw  new IOException(args[0] + ": too many arguments");
            }
            int i;
            if (flag) {
                i = 2;
            } else {
                i = 1;
            }
            File source = Utils.absoluteFileCreate(args[i]);
            File destination = Utils.absoluteFileCreate(args[i + 1]);
            if (source.exists()) {
                if ((destination.exists()) || ((source.isFile()) && ((new File(destination.getParent())).exists()))) {
                    Utils.copyFileOrDirectory(source, destination, flag);
                } else {
                    throw new IOException("cp: " + args[i] + "is a directory (not copied).");
                }
            } else {
                throw new IOException("cp: " + args[i] + "not copied");
            }
        }
    }

}
