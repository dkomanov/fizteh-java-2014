package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

public class Rm {
    public static void run(String[] args, boolean flag, int j) throws IOException {
        if (!(flag && j + 1 >= 3) && !(!flag && j + 1 >= 2)) {
            throw new IOException(args[0] + ": missing operand");
        } else {
            if (!(flag && j + 1 <= 3) && !(!flag && j + 1 >= 2)) {
                throw new IOException(args[0] + ": too many arguments");
            }
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
                    Path path = file.toPath();
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException | SecurityException e) {
                        System.err.println(e);
                    }
                } else {
                    if (file.list().length > 0) {
                        throw new IOException(args[i] + ": is a directory");
                    } else {
                        Path path = file.toPath();
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException | SecurityException e) {
                            System.err.println(e);
                        }
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
