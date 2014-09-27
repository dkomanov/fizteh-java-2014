package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cat {
    private Cat() {
        //
    }

    public static void run(String[] args) throws IOException {
        if (args.length < 2) {
            System.exit(0);
        } else {
            File file = Utils.absoluteFileCreate(args[1]);
            Path path = file.toPath();
            if (!Files.isReadable(path)) {
                throw new IOException("cat: " + args[1] + ": No such file or directory");
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
        }
    }
}

