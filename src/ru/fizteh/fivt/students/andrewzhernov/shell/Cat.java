package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Cat {
    public static void execute(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: cat <file>");
        } else {
            File file = ChangeDir.openFile(args[1]);
            if (file.isFile()) {
                InputStream input = null;
                try {
                    input = new FileInputStream(file);
                    printFile(input, System.out);
                } catch (Exception e) {
                    throw new Exception("cat: can't read file");
                } finally {
                    input.close();
                }
            } else if (file.isDirectory()) {
                throw new Exception("cat: " + args[1] + " is a directory");
            } else {
                throw new Exception("cat: " + args[1] + ": no such file or directory");
            }
        }
    }

    public static void printFile(InputStream input, OutputStream output) throws Exception {
        int index;
        byte[] buffer = new byte[4096];
        while ((index = input.read(buffer)) != -1) {
            output.write(buffer, 0, index);
        }
    }
}
