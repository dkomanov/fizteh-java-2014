package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public class Mv {
    private Mv() {
    }

    public static void run(String[] args) throws IOException {
        if (args.length < 3) {
            System.exit(0);
        }
        File source = Utils.absoluteFileCreate(args[1]);
        ;
        File destination = Utils.absoluteFileCreate(args[2]);
        if ((source.exists()) && (source.getPath().equals(destination.getPath()))) {
            return;
        }

        if ((source.exists()) && (source.isDirectory()) && (!destination.exists()) && (source.getParent().
                equals(destination.getParent()))) {
            if (!source.renameTo(destination)) {
                throw new IOException("rm: cannot remove '" + args[1] + "' : No such file or  directory");
            }
            return;
        }
        boolean flag = true;
        Utils.copyFileOrDirectory(source, destination, flag);
        Utils.deleteFileOrDirectory(source);
    }
}
