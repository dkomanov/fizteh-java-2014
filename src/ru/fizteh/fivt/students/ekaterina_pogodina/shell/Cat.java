package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cat {
    private Cat() {  }
    public static void run(String[] args, int j) throws IOException {
        if (j + 1 < 2) {
            throw new IOException(args[0] + ": missing operand");
        } else {
            if (j + 1 > 2) {
                throw new IOException(args[0] + ": too mush arguments");
            } else {
                File file = Utils.absoluteFileCreate(args[1]);
                if (file.exists()) {
                    if (file.isFile()) {
                        Path path = file.toPath();
                        if (!Files.isReadable(path)) {
                            throw new IOException("cat: " + args[1] + ": not readable");
                        }
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        while (true) {
                            int c = br.read();
                            if (c == -1) {
                                break;
                            }
                            System.out.print((char) c);
                        }
                        br.close();
                        System.out.print("\n");
                    } else {
                        throw new IOException(args[0] + "is a directory");
                    }
                } else {
                    throw new IOException(args[0] + ": No such file or directory");
                }
            }

        }
    }
}

