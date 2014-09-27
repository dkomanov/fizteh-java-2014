package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public class Rm {
    public static void run(String[] args, boolean flag) throws IOException {
        if (args.length < 2) {
            System.out.print("Not  enougth arguments");
        }
        int i;
        if (flag) {
            i = 2;
        } else {
            i = 1;
        }
        File file = Utils.absoluteFileCreate(args[i]);
        if (file.exists()) {
            if (!flag) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    if (file.list().length > 0) {
                        throw new IOException(args[i] + ": is a directory");
                    } else {
                        file.delete();
                    }
                }
            } else {
                Utils.deleteFileOrDirectory(file);
            }
        } else {
            throw new IOException("cannot remove '" + args[i] + "': no such file or directory");
        }
    }

}
