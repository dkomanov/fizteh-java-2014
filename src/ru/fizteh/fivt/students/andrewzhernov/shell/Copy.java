package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Copy {
    public static void execute(String[] args) throws Exception {
        File source = null;
        File destination = null;
        if (args.length == 3) {
            source = ChangeDir.openFile(args[1]);
            destination = ChangeDir.openFile(args[2]);
            if (source.isDirectory()) {
                throw new Exception("cp: " + args[1] + " is a directory");
            }
        } else if (args.length == 4 && args[1].equals("-r")) {
            source = ChangeDir.openFile(args[2]);
            destination = ChangeDir.openFile(args[3]);
        } else {
            throw new Exception("Usage: cp [-r] <source> <destination>");
        }
        if (source.equals(destination)) {
            throw new Exception("cp: '" + source.getPath() + "' and '" + destination.getPath() + "' are the same");
        }
        if (!source.exists()) {
            throw new Exception("cp: " + source.getPath() + ": no such file or directory");
        }
        if (destination.exists() && destination.isDirectory()) {
            destination = new File(destination, source.getName());
        }
        if (!destination.exists()) {
            if (source.isDirectory() && !destination.mkdir() || source.isFile() && !destination.createNewFile()) {
                throw new Exception("cp: " + destination.getPath() + ": no such file or directory");
            }
        }
        copy(source, destination);
    }

    private static void copy(File source, File destination) throws Exception {
        if (source.isFile()) {
            copyFile(source, destination);
        } else {
            copyDir(source, destination);
        }
    }

    private static void copyFile(File source, File destination) throws Exception {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(destination);
            Cat.printFile(input, output);
        } catch (Exception e) {
            throw new Exception("cp: can't read file");
        } finally {
            input.close();
            output.close();
        }
    }

    private static void copyDir(File source, File destination) throws Exception {
        destination.mkdir();
        String[] list = source.list();
        for (String fileName : list) {
            copy(new File(source, fileName), new File(destination, fileName));
        }
    }
}
